package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.services.StudentOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * Endpoint for getting all StudentOffers.
     *
     * @return 200 OK ResponseEntity with a list of StudentOffers if the requester has permission.
     *          It is to be decided how the authentication will handle the other case.
     */
    @GetMapping("/student/getAllOffers")
    public ResponseEntity<List<StudentOffer>> getAllStudentOffers() {
        // We have to check if the requester has the rights to view the student offers.
        List<StudentOffer> studentOffers = studentOfferService.getOffers();
        return ResponseEntity.ok(studentOffers);
    }

    /**
     * Endpoint for getting all offers of the same student.
     *
     * @param studentId the id of the student.
     * @return 200 OK ResponseEntity with a list of StudentOffers if valid
     *          and 400 BAD Request with error message otherwise.
     */
    @GetMapping("/student/getOffers/{studentId}")
    public ResponseEntity<?> getStudentOffersById(@PathVariable String studentId) {
        // Check if student with that id exists.
        // Also check if the one who seeks the info can get it.
        try {
            List<StudentOffer> offersById = studentOfferService.getOffersById(studentId);
            return ResponseEntity.ok(offersById);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }
}
