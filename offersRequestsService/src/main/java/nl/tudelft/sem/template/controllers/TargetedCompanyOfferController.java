package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.services.TargetedCompanyOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TargetedCompanyOfferController {

    @Autowired
    private transient TargetedCompanyOfferService targetedCompanyOfferService;

    /** Endpoint for creating TargetedCompanyOffers.
     *
     * @param targetedCompanyOffer TargetedCompanyOffer that needs to be saved
     * @return 201 CREATED ResponseEntity with saved TargetedCompanyOffer in body if valid
     *         otherwise 400 BAD REQUEST with error message.
     */
    @PostMapping("/company/targeted/create/{id}")
    public ResponseEntity<?> saveTargetedCompanyOffer(
        @RequestBody TargetedCompanyOffer targetedCompanyOffer,
        @PathVariable Long id) {
        //Here we will also get authorization checks
        try {
            return new ResponseEntity<>(targetedCompanyOfferService
                .saveOffer(targetedCompanyOffer, id),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint for getting TargetedCompanyOffers by a company, which created them.
     *
     * @param companyId - the company's id
     * @return 200 OK and a list of offers if everything goes smoothly,
     *          else we return 400 Bad_Request and the message of the error which occurred.
     */
    @GetMapping("/company/targeted/getOffersById/{companyId}")
    public ResponseEntity<?> getCompanyOffersById(@PathVariable String companyId) {
        //Authenticate and check if the requester is a company.
        try {
            List<TargetedCompanyOffer> offers =
                    targetedCompanyOfferService.getOffersById(companyId);
            return ResponseEntity.ok(offers);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }

    /**
     * Endpoint for getting TargetedCompanyOffers by a company, which created them.
     *
     * @param studentOfferId - the offer's id.
     * @return 200 OK and a list of offers targeting the StudentOffer if everything goes smoothly,
     *          else we return 400 Bad_Request and the message of the error which occurred.
     */
    @GetMapping("/company/targeted/getOffersByOffer/{studentOfferId}")
    public ResponseEntity<?> getCompanyOffersByStudentOffer(@PathVariable Long studentOfferId) {
        try {
            List<TargetedCompanyOffer> offers =
                    targetedCompanyOfferService.getOffersByStudentOffer(studentOfferId);
            return ResponseEntity.ok(offers);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }
}
