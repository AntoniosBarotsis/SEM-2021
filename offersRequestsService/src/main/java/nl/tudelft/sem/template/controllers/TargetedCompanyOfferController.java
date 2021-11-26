package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.services.TargetedCompanyOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/company/createtargeted/{id}")
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
}
