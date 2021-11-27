package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OfferController {

    @Autowired
    private transient OfferService offerService;

    /** Endpoint for getting all offers of a user.
     *
     * @param username String of the username
     * @return 200 OK ResponseEntity if correct with all StudentOffers in the body.
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getAllByUsername(@PathVariable String username) {
        return new ResponseEntity<>(offerService.getAllByUsername(username), HttpStatus.OK);
    }

}
