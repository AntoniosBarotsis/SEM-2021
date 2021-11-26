package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.services.TargetedCompanyOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/company/targeted/create")
    public ResponseEntity<?> saveTargetedCompanyOffer(
            @RequestBody TargetedCompanyOffer targetedCompanyOffer) {
        //Here we will also get authorization checks
        try {
            return new ResponseEntity<>(targetedCompanyOfferService.saveOffer(targetedCompanyOffer),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/company/targeted/getOffers/{userId}")
    public ResponseEntity<?> getAllCompanyOffersById(@PathVariable String userId){
        //Authenticate and check if the requester is a company or a student.
        //For now, I will use a boolean - true if company, false if student.
        try{
            List<TargetedCompanyOffer> offers = targetedCompanyOfferService.getOffersById(userId, true);
            return ResponseEntity.ok(offers);
        } catch (IllegalArgumentException exception){
            exception.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }
}
