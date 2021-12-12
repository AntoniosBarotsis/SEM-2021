package nl.tudelft.sem.template.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.naming.NoPermissionException;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.entities.dtos.ContractDto;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.ContractCreationException;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;
    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;
    @Autowired
    private transient RestTemplate restTemplate;
    @Autowired
    private transient Utility utility;

    /**
     * Service, which returns all active StudentOffers, which are stored in the repository.
     *
     * @return - A list of Pending Student Offers.
     */
    public List<StudentOffer> getOffers() {
        return studentOfferRepository.findAllActive();
    }

    /**
     * Service, which returns all offers created by a Student.
     *
     * @param studentId - the ID of the Student.
     * @return - A list of the Student's Offers.
     */
    public List<StudentOffer> getOffersById(String studentId) {
        utility.userExists(studentId, restTemplate);


        List<StudentOffer> offer = studentOfferRepository.findAllByStudentId(studentId);
        if (offer.isEmpty()) {
            throw new IllegalArgumentException("No such student has made offers!");
        }
        return offer;
    }

    /**
     * Service, which accepts the given targeted offer and declines all others.
     *
     * @param userName - the id of the Student, who wants to accept the offer.
     * @param userRole - the role of the Student.
     * @param targetedCompanyOfferId - the id of the accepted offer.
     * @throws NoPermissionException - is thrown
     *      if the user doesn't have permission to accept the offer.
     * @throws ContractCreationException - if the request wasn't successful.
     */
    public ContractDto acceptOffer(
            String userName, String userRole, Long targetedCompanyOfferId)
            throws NoPermissionException, ContractCreationException {
        Optional<TargetedCompanyOffer> targetedCompanyOffer =
                targetedCompanyOfferRepository.findById(targetedCompanyOfferId);
        if (targetedCompanyOffer.isEmpty()) {
            throw new IllegalArgumentException("ID is not valid!");
        }
        StudentOffer offer = targetedCompanyOffer.get().getStudentOffer();
        if (!offer
                .getStudentId()
                .equals(userName) || !userRole.equals("STUDENT")) {
            throw new NoPermissionException("User not allowed to accept this TargetedOffer");
        }
        if (offer.getStatus() != Status.PENDING
                || targetedCompanyOffer.get().getStatus() != Status.PENDING) {
            throw new IllegalArgumentException(
                    "The StudentOffer or TargetedRequest is not active anymore!");
        }

        // First try to create the contract between the 2 parties.
        // If the contract creation doesn't succeed then the offer isn't accepted.
        // Throws exception if error:
        TargetedCompanyOffer tco = targetedCompanyOffer.get();
        ContractDto contract;
        contract = utility.createContract(tco.getCompanyId(), userName,
                tco.getHoursPerWeek(), tco.getTotalHours(), offer.getPricePerHour(), restTemplate);

        List<TargetedCompanyOffer> offers = offer.getTargetedCompanyOffers();

        for (TargetedCompanyOffer t : offers) {
            if (!t.equals(targetedCompanyOffer.get())) {
                t.setStatus(Status.DECLINED);
            } else {
                t.setStatus(Status.ACCEPTED);
            }
            targetedCompanyOfferRepository.save(t);
        }

        offer.setStatus(Status.DISABLED);
        studentOfferRepository.save(offer);

        return contract;
    }

    /**
     * Service, which updates a StudentOffer.
     *
     * @param studentOffer - The updated offer, which will be now stored.
     */
    public void updateStudentOffer(StudentOffer studentOffer) {
        StudentOffer current =  studentOfferRepository.getById(studentOffer.getId());
        if (current == null) {
            throw new IllegalArgumentException("This StudentOffer does not exist!");
        }
        if (current.getStatus() != studentOffer.getStatus()) {
            throw new IllegalArgumentException("You are not allowed to edit the Status");
        }

        super.saveOffer(studentOffer);
    }

    /**
     * Service, which gets all offers, whose fields are equal to the keyword.
     *
     * @param keyWord - the parameter we filter by.
     * @return - a list of Student Offers.
     * @throws UnsupportedEncodingException  -is thrown, if the input is invalid.
     */
    public List<StudentOffer> getByKeyWord(String keyWord) throws UnsupportedEncodingException {
        String decoded = URLDecoder.decode(keyWord, StandardCharsets.UTF_8);

        return studentOfferRepository.getAllByKeyWord(decoded);
    }

    /**
     * Service, which gets all offers, whose expertises contain the criteria.
     *
     * @param expertises - the parameter we filter by.
     * @return - a list of Student Offers.
     * @throws UnsupportedEncodingException  -is thrown, if the input is invalid.
     */
    public List<StudentOffer> getByExpertises(List<String> expertises)
            throws UnsupportedEncodingException {
        for (int i = 0; i < expertises.size(); i++) {
            String decoded =
                    URLDecoder.decode(expertises.get(i), StandardCharsets.UTF_8);
            expertises.set(i, decoded);
        }

        List<StudentOffer> offers = studentOfferRepository.findAllActive();
        offers.removeIf(offer -> Collections.disjoint(offer.getExpertise(), expertises));

        return offers;
    }
}
