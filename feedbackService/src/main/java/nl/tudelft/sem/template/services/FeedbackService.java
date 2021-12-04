package nl.tudelft.sem.template.services;

import com.netflix.discovery.shared.Pair;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import nl.tudelft.sem.template.controllers.UserRole;
import nl.tudelft.sem.template.domain.Feedback;
import nl.tudelft.sem.template.domain.Response;
import nl.tudelft.sem.template.domain.dtos.FeedbackRequest;
import nl.tudelft.sem.template.domain.dtos.FeedbackRequestWrapper;
import nl.tudelft.sem.template.domain.dtos.FeedbackResponse;
import nl.tudelft.sem.template.exceptions.FeedbackNotFoundException;
import nl.tudelft.sem.template.exceptions.InvalidFeedbackDetailsException;
import nl.tudelft.sem.template.exceptions.InvalidRoleException;
import nl.tudelft.sem.template.exceptions.InvalidUserException;
import nl.tudelft.sem.template.exceptions.NoExistingContractException;
import nl.tudelft.sem.template.exceptions.UserServiceUnavailableException;
import nl.tudelft.sem.template.repositories.FeedbackRepository;
import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private RestTemplate restTemplate;

    public FeedbackResponse getById(Long id) {
        var res = feedbackRepository.findById(id);

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
            var baseUserUrl = "http://users-service/";
            var urlRecipient = baseUserUrl + feedbackRequest.getTo();

            // Get the target role
            UserRole targetRole;
            if (UserRole.valueOf(userRole) == UserRole.STUDENT) {
                targetRole = UserRole.COMPANY;
            } else if (UserRole.valueOf(userRole) == UserRole.COMPANY) {
                targetRole = UserRole.STUDENT;
            } else {
                throw new InvalidRoleException("Role " + userRole + " is invalid.");
            }

            var resTo =
                restTemplate.getForObject(urlRecipient, FeedbackRequestWrapper.class);

            if (resTo == null) {
                throw new UserServiceUnavailableException();
            }

            if (UserRole.valueOf(resTo.getData().getRole()) != targetRole) {
                throw new InvalidUserException("The recipient has the same role as the author.");
            }

            if (resTo.getErrorMessage() != null) {
                throw new InvalidUserException("Recipient does not exist.");
            }

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

            var contractUrl = "http://contract-service/" + companyName + "/" + studentName;

            // Check if there exists a contract between the two parties.
            try {
                restTemplate
                    .getForEntity(contractUrl, String.class)
                    .getStatusCode();
            } catch (HttpClientErrorException e) {
                var msg = "No contract found between " + feedbackRequest.getFrom()
                    + " and " + feedbackRequest.getTo();

                throw new NoExistingContractException(msg);
            }

        } catch (RestClientException e) {
            throw new UserServiceUnavailableException(e.getMessage());
        } catch (IllegalArgumentException e) { // UserRole.valueOf(userRole) can throw this
            throw new InvalidRoleException("Role " + userRole + " is invalid.");
        }

        var feedback = Feedback.from(feedbackRequest);

        var res = feedbackRepository.save(feedback);

        return new Pair<>(res.to(), res.getId());
    }
}
