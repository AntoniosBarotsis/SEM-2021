package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.responses.Response;
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
     * @return 200 OK ResponseEntity
     * if correct with a response of all StudentOffers in the body.
     */
    @GetMapping("/offers/{username}")
    public ResponseEntity<Response<Map<String, List<Offer>>>>
        getAllByUsername(@PathVariable String username) {
        Response<Map<String, List<Offer>>> response =
                new Response<>(offerService.getAllByUsername(username), null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
