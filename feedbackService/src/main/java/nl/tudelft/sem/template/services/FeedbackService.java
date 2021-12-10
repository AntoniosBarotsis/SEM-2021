package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.domain.Feedback;
import nl.tudelft.sem.template.domain.dtos.enums.Status;
import nl.tudelft.sem.template.domain.dtos.enums.UserRole;
import nl.tudelft.sem.template.domain.dtos.requests.FeedbackRequest;
import nl.tudelft.sem.template.domain.dtos.responses.ContractResponse;
import nl.tudelft.sem.template.domain.dtos.responses.FeedbackResponse;
import nl.tudelft.sem.template.domain.dtos.responses.UserRoleResponseWrapper;
import nl.tudelft.sem.template.exceptions.ContractNotExpiredException;
import nl.tudelft.sem.template.exceptions.FeedbackAlreadyExistsException;
import nl.tudelft.sem.template.exceptions.FeedbackNotFoundException;
import nl.tudelft.sem.template.exceptions.InvalidFeedbackDetailsException;
import nl.tudelft.sem.template.exceptions.InvalidRoleException;
import nl.tudelft.sem.template.exceptions.InvalidUserException;
import nl.tudelft.sem.template.exceptions.NoExistingContractException;
import nl.tudelft.sem.template.exceptions.UserServiceUnavailableException;
import nl.tudelft.sem.template.repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class FeedbackService {
    @Autowired
    private transient FeedbackRepository feedbackRepository;
    @Autowired
    private transient RestTemplate restTemplate;

    public FeedbackResponse getById(Long id) {
        Optional<Feedback> res = feedbackRepository.findById(id);

        if (res.isEmpty()) {
            throw new FeedbackNotFoundException("Feedback with id " + id + " does not exist.");
        }

        return res.get().to();
    }

    public Pair<FeedbackResponse, Long> create(FeedbackRequest feedbackRequest, String userName,
                                               String userRole) {
        if (feedbackRequest.getFrom() == null) {
            feedbackRequest.setFrom(userName);
        }

        try {
            if (userName.equals(feedbackRequest.getTo()) ||
                feedbackRequest.getTo().equals(feedbackRequest.getFrom())) {
                throw new InvalidFeedbackDetailsException("Cant add a review of yourself");
            }

            // Check if student/company exist
            // Target role should be the opposite of the user role
            UserRole targetRole =
                getRecipientRole(feedbackRequest, userRole);

            // Author and recipient must have a contract to leave feedback.
            // If the target role is a student then the author is a company and vice-versa.
            String companyName;
            String studentName;
            if (targetRole == UserRole.STUDENT) {
                companyName = feedbackRequest.getFrom();
                studentName = feedbackRequest.getTo();
            } else {
                companyName = feedbackRequest.getTo();
                studentName = feedbackRequest.getFrom();
            }

            String contractUrl = "http://contract-service/" + companyName + "/" + studentName
                    + "/mostRecent";

            // Check if there exists a contract between the two parties.
            checkExistingContract(feedbackRequest, contractUrl);

        } catch (RestClientException e) {
            throw new UserServiceUnavailableException(e.getMessage());
        } catch (IllegalArgumentException e) { // UserRole.valueOf(userRole) can throw this
            throw new InvalidRoleException("Role " + userRole + " is invalid.");
        }

        Feedback feedback = Feedback.from(feedbackRequest);

        Feedback res = feedbackRepository.save(feedback);

        return Pair.of(res.to(), res.getId());
    }

    private void checkExistingContract(FeedbackRequest feedbackRequest, String contractUrl) {
        try {

            // Headers of the request:
            HttpHeaders headers = new HttpHeaders();
            headers.add("x-user-name", feedbackRequest.getFrom());

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // getForObject doesn't support headers, use exchange instead:
            ContractResponse contract = restTemplate
                    .exchange(contractUrl, HttpMethod.GET, requestEntity, ContractResponse.class)
                    .getBody();

            if (Status.valueOf(contract.getStatus()) == Status.ACTIVE) {
                String msg = "Can't leave feedback while contract is still active.";
                throw new ContractNotExpiredException(msg);
            }

            // Should not be able to give feedback more than once
            List<Feedback> existingFeedbacks = feedbackRepository
                .hasReviewedBefore(
                    feedbackRequest.getFrom(),
                    feedbackRequest.getTo(),
                    feedbackRequest.getContractId()
                );

            if (!existingFeedbacks.isEmpty()) {
                String msg = "You have already given feedback for this contract";
                throw new FeedbackAlreadyExistsException(msg);
            }
        } catch (HttpClientErrorException e) {
            String msg = "No contract found between " + feedbackRequest.getFrom()
                + " and " + feedbackRequest.getTo();

            throw new NoExistingContractException(msg);
        }
    }

    private UserRole getRecipientRole(FeedbackRequest feedbackRequest, String userRole) {
        String baseUserUrl = "http://users-service/";
        String urlRecipient = baseUserUrl + feedbackRequest.getTo();

        // Get the target role
        UserRole targetRole =
            UserRole.valueOf(userRole) == UserRole.STUDENT ?
                UserRole.COMPANY : UserRole.STUDENT;

        UserRoleResponseWrapper recipientUser =
            restTemplate.getForObject(urlRecipient, UserRoleResponseWrapper.class);

        if (recipientUser == null) {
            throw new UserServiceUnavailableException();
        }

        if (recipientUser.getErrorMessage() != null) {
            throw new InvalidUserException("Recipient does not exist.");
        }

        if (UserRole.valueOf(recipientUser.getData().getRole()) != targetRole) {
            throw new InvalidUserException("The recipient has the same role as the author.");
        }

        return targetRole;
    }
}
