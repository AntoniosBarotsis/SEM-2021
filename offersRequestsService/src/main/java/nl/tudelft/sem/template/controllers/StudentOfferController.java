package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.services.StudentOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentOfferController {

    @Autowired
    private transient StudentOfferService studentOfferService;

    /**
     * Endpoint for creating StudentOffers.
     *
     * @param studentOffer StudentOffer than needs to bed created.
     * @return 201 CREATED ResponseEntity with saved StudentOffer in body if valid
     *          otherwise 400 BAD REQUEST with error message.
     */
    @PostMapping("/student/create")
    public ResponseEntity<?> saveStudentOffer(@RequestBody StudentOffer studentOffer) {
        //Here we will also get authorization checks,
        // who is the user posting and is their ID in the studentOffer
        try {
            return new ResponseEntity<>(studentOfferService.saveOffer(studentOffer),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
