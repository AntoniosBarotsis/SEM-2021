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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OfferController {

    @Autowired
    private transient OfferService offerService;

    /** Endpoint for getting all offers of a user.
     *
     * @param userName String of the username
     * @return 200 OK ResponseEntity
     *     if correct with a response of all StudentOffers in the body.
     *     401 UNAUTHORIZED if user not authenticated.
     */
    @GetMapping("/offers/")
    public ResponseEntity<Response<Map<String, List<Offer>>>>
        getAllByUsername(@RequestHeader("x-user-name") String userName) {
        if (userName.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, "User is not authenticated"));
        }
        Response<Map<String, List<Offer>>> response =
                new Response<>(offerService.getAllByUsername(userName), null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
