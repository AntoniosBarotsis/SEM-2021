package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.entities.dtos.Response;
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

    /**
     * Endpoint for creating TargetedCompanyOffers.
     *
     * @param targetedCompanyOffer TargetedCompanyOffer that needs to be saved
     * @return 201 CREATED ResponseEntity
     * with a Response containing the saved TargetedCompanyOffer in body if valid
     * otherwise 400 BAD REQUEST with a Response with the error message.
     */
    @PostMapping("/company/targeted/create/{id}")
    public ResponseEntity<Response<Offer>> saveTargetedCompanyOffer(
        @RequestBody TargetedCompanyOffer targetedCompanyOffer,
        @PathVariable Long id) {
        //Here we will also get authorization checks
        Response<Offer> responseSave;
        try {
            responseSave =
                new Response<>(targetedCompanyOfferService
                    .saveOffer(targetedCompanyOffer, id),
                    null);

            return new ResponseEntity<>(
                responseSave,
                HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            responseSave =
                new Response<>(null,
                    e.getMessage());

            return new ResponseEntity<>(responseSave,
                HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint for getting TargetedCompanyOffers by a company, which created them.
     *
     * @param companyId - the company's id
     * @return 200 OK and a Response with a list of offers if everything goes smoothly,
     * else we return 400 Bad_Request and
     * the Response containing the message of the error which occurred.
     */
    @GetMapping("/company/targeted/getOffersById/{companyId}")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
    getCompanyOffersById(@PathVariable String companyId) {
        //Authenticate and check if the requester is a company.
        Response<List<TargetedCompanyOffer>> responseOffersById;
        try {
            responseOffersById =
                new Response<>(
                    targetedCompanyOfferService.getOffersById(companyId),
                    null);

            return ResponseEntity.ok(responseOffersById);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();

            responseOffersById =
                new Response<>(
                    null,
                    exception.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseOffersById);
        }
    }

    /**
     * Endpoint for getting TargetedCompanyOffers by a company, which created them.
     *
     * @param studentOfferId - the offer's id.
     * @return 200 OK and a Response which contains a list of offers targeting the StudentOffer
     * if everything goes smoothly,
     * else we return 400 Bad_Request
     * and a Response with the message of the error which occurred.
     */
    @GetMapping("/company/targeted/getOffersByOffer/{studentOfferId}")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
    getCompanyOffersByStudentOffer(@PathVariable Long studentOfferId) {
        Response<List<TargetedCompanyOffer>> offers;
        try {
            offers =
                new Response<>(
                    targetedCompanyOfferService
                        .getOffersByStudentOffer(studentOfferId),
                    null);

            return ResponseEntity.ok(offers);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();

            offers =
                new Response<>(
                    null,
                    exception.getMessage()
                );
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(offers);
        }
    }

    /**
     * Endpoint which provides All Offers that target a student.
     *
     * @param student - the targeted student
     * @return - a Response with a list of Offers or the error message.
     */
    @GetMapping("/company/targeted/student/{student}")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
    getAllByStudent(@PathVariable String student) {

        Response<List<TargetedCompanyOffer>> offers;
        offers =
            new Response<>(
                targetedCompanyOfferService
                    .getAllByStudent(student),
                null);

        return new ResponseEntity<>(
            offers,
            HttpStatus.OK);

    }
}
