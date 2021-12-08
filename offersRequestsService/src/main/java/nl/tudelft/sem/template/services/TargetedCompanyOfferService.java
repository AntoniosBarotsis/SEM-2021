package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.entities.dtos.Response;
import nl.tudelft.sem.template.exceptions.LowRatingException;
import nl.tudelft.sem.template.exceptions.UpstreamServiceException;
import nl.tudelft.sem.template.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TargetedCompanyOfferService extends OfferService {

    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;

    @Autowired
    private transient RestTemplate restTemplate;

    /**
     * Method for saving a TargetedCompanyOffer.
     *
     * @param targetedCompanyOffer Offer we want to save.
     * @param id                   Long with the StudentOffer this CompanyOffer targets.
     * @return The saved Offer.
     * @throws IllegalArgumentException Thrown if any of the conditions are not met.
     */
    public Offer saveOffer(TargetedCompanyOffer targetedCompanyOffer, Long id)
        throws IllegalArgumentException, LowRatingException, UpstreamServiceException {
        StudentOffer studentOffer = studentOfferRepository.getById(id);
        if (studentOffer == null) {
            throw new IllegalArgumentException("Student offer does not exist");
        }
        targetedCompanyOffer.setStudentOffer(studentOffer);
        return super.saveOffer(targetedCompanyOffer);
    }

    /**
     * Calls saveOffer, catches any exceptions and returns a ResponseEntity based
     * on the result or thrown exceptions.
     *
     * @param offer Offer to be saved.
     * @param id Id of the StudentOffer this offer targets.
     * @return ResponseEntity with the result of the saveOffer method.
     */
    public ResponseEntity<Response<Offer>> saveOfferWithResponse(Offer offer, Long id) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(saveOffer(offer)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(null, e.getMessage()));
        } catch (LowRatingException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null, e.getMessage()));
        } catch (UpstreamServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new Response<>(null, e.getMessage()));
        }
    }

    /**
     * Service, which provides Targeted offers, created by a specific Company.
     *
     * @param companyId - the ID of the Company.
     * @return - A list of Targeted Requests, which are all created by the Company.
     */
    public List<TargetedCompanyOffer> getOffersById(String companyId) {
        List<TargetedCompanyOffer> offers =
            targetedCompanyOfferRepository.findAllByCompanyId(companyId);
        if (offers.size() == 0) {
            throw new IllegalArgumentException("No such company has made offers!");
        }

        return offers;
    }

    /** Service, which provides Targeted offers, related to a specific Student offer.
     *
     * @param studentOfferId - the id of the Student offer that we want to specify.
     * @param username Name of the person making the request.
     * @return List of TargetedCompanyOffers belonging to the StudentOffer.
     */
    public List<TargetedCompanyOffer> getOffersByStudentOffer(Long studentOfferId,
                                                              String username) {
        StudentOffer studentOffer = studentOfferRepository.getById(studentOfferId);
        if (studentOffer == null) {
            throw new IllegalArgumentException("Student offer does not exist");
        }
        if (!username.equals(studentOffer.getStudentId())) {
            throw new UserNotAuthorException(username);
        }
        return targetedCompanyOfferRepository.findAllByStudentOffer(studentOffer);
    }

    /**
     * Service, which provides a list of offers, which target a specific student.
     *
     * @param student - the targeted student.
     * @return - a list of TargetedCompanyOffers.
     */
    public List<TargetedCompanyOffer> getAllByStudent(String student) {
        return targetedCompanyOfferRepository.getAllByStudent(student);
    }
}
