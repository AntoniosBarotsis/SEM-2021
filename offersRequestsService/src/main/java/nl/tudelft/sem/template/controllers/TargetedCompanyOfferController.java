package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.responses.Response;
import nl.tudelft.sem.template.services.TargetedCompanyOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TargetedCompanyOfferController {

    @Autowired
    private transient TargetedCompanyOfferService targetedCompanyOfferService;

    private final transient String nameHeader = "x-user-name";
    private final transient String roleHeader = "x-user-role";
    private final transient String authenticationError = "User is not authenticated";

    /** Endpoint for creating TargetedCompanyOffers.
     *
     * @param userName Name of person making request.
     * @param userRole Role of person making request.
     * @param targetedCompanyOffer TargetedCompanyOffer that needs to be saved.
     * @param id Id of StudentOffer that TargetedCompanyOffer targets.
     * @return ResponseEntity that can take various codes.
     *          401 OK if user not authenticated.
     *          403 UNAUTHORIZED if user not Company or not author of offer.
     *          400 BAD REQUEST is conditions are not met.
     *          201 CREATED if successful with offer in the body.
     */
    @PostMapping("/company/targeted/create/{id}")
    public ResponseEntity<Response<Offer>> saveTargetedCompanyOffer(
            @RequestHeader(nameHeader) String userName,
            @RequestHeader(roleHeader) String userRole,
            @RequestBody TargetedCompanyOffer targetedCompanyOffer,
            @PathVariable Long id) {
        if (userName.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, authenticationError));
        }
        if (!targetedCompanyOffer.getCompanyId().equals(userName) || !userRole.equals("COMPANY")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null,
                            "User not allowed to post this TargetedCompanyOffer"));
        }
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

    /** Endpoint for getting TargetedCompanyOffers by a company, which created them.
     *
     * @param userName Name of person making the request.
     * @param userRole Role of person making the request.
     * @return ResponseEntity that can take various codes.
     *          401 UNAUTHORIZED if user not authenticated.
     *          403 FORBIDDEN if user not a company.
     *          200 OK if successful with List in body.
     */
    @GetMapping("/company/targeted/getOffersById/")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
        getCompanyOffersById(@RequestHeader(nameHeader) String userName,
                             @RequestHeader(roleHeader) String userRole) {
        //Authenticate and check if the requester is a company.
        if (userName.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, authenticationError));
        }
        if (!userRole.equals("COMPANY")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null, "User is not a company"));
        }
        Response<List<TargetedCompanyOffer>> responseOffersById;
        try {
            responseOffersById =
                    new Response<>(
                            targetedCompanyOfferService.getOffersById(userName),
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
     * Endpoint for getting TargetedCompanyOffers by a StudentOffer.
     *
     * @param userName Name of user making the request.
     * @param studentOfferId - the offer's id.
     * @return ResponseEntity that can take various codes.
     *          401 UNAUTHORIZED if not authenticated.
     *          403 FORBIDDEN if studentOffer doesn't belong to user making request.
     *          200 OK if successful with list of offers that belong to the StudentOffer.
     *          400 BAD REQUEST if conditions are not met.
     */
    @GetMapping("/company/targeted/getOffersByOffer/{studentOfferId}")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
        getCompanyOffersByStudentOffer(@RequestHeader(nameHeader) String userName,
                                       @PathVariable Long studentOfferId) {
        if (userName.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, authenticationError));
        }
        Response<List<TargetedCompanyOffer>> offers;
        try {
            offers =
                    new Response<>(
                            targetedCompanyOfferService
                                    .getOffersByStudentOffer(studentOfferId, userName),
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
        } catch (UserNotAuthorException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null, e.getMessage()));
        }
    }

    /**
     * Endpoint for getting al TargetedCompanyOffers of a student.
     *
     * @param userName Name of the user making request.
     * @param userRole Role of the user making request.
     * @return ResponseEntity that can take various codes.
     *          401 UNAUTHORIZED if user not authenticated.
     *          403 FORBIDDEN if user not a student.
     *          400 BAD REQUEST if conditions of the request are not met.
     *          200 OK if successful with a List of TargetedCompanyRequests in body.
     */
    @GetMapping("/company/targeted/student/")
    public ResponseEntity<Response<List<TargetedCompanyOffer>>>
        getAllByStudent(@RequestHeader(nameHeader) String userName,
                        @RequestHeader(roleHeader) String userRole) {
        if (userName.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, authenticationError));
        }
        if (!userRole.equals("STUDENT")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null, "User is not a student"));
        }
        Response<List<TargetedCompanyOffer>> offers;
        try {
            offers =
                    new Response<>(
                            targetedCompanyOfferService
                                    .getAllByStudent(userName),
                            null);

            return new ResponseEntity<>(
                    offers,
                    HttpStatus.OK);
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
}
