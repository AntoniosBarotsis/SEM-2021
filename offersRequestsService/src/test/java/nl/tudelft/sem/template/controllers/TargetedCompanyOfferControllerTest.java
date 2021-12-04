package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.responses.Response;
import nl.tudelft.sem.template.services.TargetedCompanyOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureMockMvc
class TargetedCompanyOfferControllerTest {

    @Autowired
    private transient TargetedCompanyOfferController targetedCompanyOfferController;

    @MockBean
    private transient TargetedCompanyOfferService targetedCompanyOfferService;

    private transient StudentOffer studentOffer;
    private transient TargetedCompanyOffer targetedCompanyOffer;
    private transient TargetedCompanyOffer targetedCompanyOfferTwo;
    private transient String student;
    private transient String company;
    private transient String companyRole;
    private transient String studentRole;
    private final transient String authenticationError = "User is not authenticated";

    @BeforeEach
    void setup() {
        final List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        student = "Student";
        company = "Company";
        studentRole = "STUDENT";
        companyRole = "COMPANY";
        studentOffer = new StudentOffer("This is a title", "This is a description",
                20, 520,
            expertise, Status.DISABLED,
            32, student);
        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
           "This is a company description",
            20, 520, expertise, Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            company, null);
        targetedCompanyOfferTwo = new TargetedCompanyOffer(
                "We're company X and we're interested in your skills",
                "We are involved in the FinTech field.",
                10, 200, expertise, Status.PENDING,
                Arrays.asList("Statistics", "Python", "Finance"),
                "MoneyNL", studentOffer);
    }

    @Test
    void saveTargetedCompanyOfferValid() {
        TargetedCompanyOffer targetedCompanyOffer2 = new TargetedCompanyOffer(
            "This is a company title", "This is a company description",
            20, 520,
            Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            "Company", studentOffer);

        Mockito.when(targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L))
            .thenReturn(targetedCompanyOffer2);

        ResponseEntity<Response<Offer>> response = targetedCompanyOfferController
            .saveTargetedCompanyOffer(company, companyRole, targetedCompanyOffer, 33L);
        Response<Offer> res = new Response<>(targetedCompanyOffer2, null);

        assertEquals(res, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void saveTargetedCompanyOfferIllegal() {
        targetedCompanyOffer.setHoursPerWeek(21);
        String errorMessage = "Offer exceeds 20 hours per week";
        Mockito.when(targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L))
            .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<Response<Offer>> response = targetedCompanyOfferController
            .saveTargetedCompanyOffer(company, companyRole, targetedCompanyOffer, 33L);
        Response<Offer> resError = new Response<>(null, errorMessage);

        assertEquals(resError, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCompanyOffersByIdTextPass() {
        List<TargetedCompanyOffer> returned = new ArrayList<>();
        returned.add(targetedCompanyOfferTwo);

        Mockito.when(targetedCompanyOfferService.getOffersById("MoneyNL"))
            .thenReturn(returned);

        Response<List<TargetedCompanyOffer>> res =
                new Response<>(returned, null);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                new ResponseEntity<>(res, HttpStatus.OK);

        assertEquals(response,
            targetedCompanyOfferController.getCompanyOffersById("MoneyNL", companyRole));
    }

    @Test
    void getCompanyOffersByIdTextFail() {
        String message = "No such company has made offers!";

        Mockito.when(targetedCompanyOfferService.getOffersById(any()))
            .thenThrow(new IllegalArgumentException(message));

        Response<List<TargetedCompanyOffer>> resErrorMessage =
                new Response<>(null, message);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
            new ResponseEntity<>(resErrorMessage, HttpStatus.BAD_REQUEST);

        assertEquals(response,
            targetedCompanyOfferController.getCompanyOffersById("Company", companyRole));
    }

    @Test
    void getCompanyOffersByStudentOfferTestPass() {
        List<TargetedCompanyOffer> returned = new ArrayList<>();
        returned.add(targetedCompanyOfferTwo);

        Long studentOfferId = targetedCompanyOfferTwo
                .getStudentOffer().getId();

        Mockito.when(targetedCompanyOfferService
                        .getOffersByStudentOffer(studentOfferId, student))
                .thenReturn(returned);

        Response<List<TargetedCompanyOffer>> resPositive =
                new Response<>(returned, null);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                new ResponseEntity<>(resPositive, HttpStatus.OK);

        assertEquals(response,
                targetedCompanyOfferController
                        .getCompanyOffersByStudentOffer(student, studentOfferId));
    }

    @Test
    void getCompanyOffersByStudentOfferTestFail() {
        String message = "Student offer does not exist";

        Mockito.when(targetedCompanyOfferService.getOffersByStudentOffer(3L, student))
                .thenThrow(new IllegalArgumentException(message));

        Response<List<TargetedCompanyOffer>> resErrorMessage =
                new Response<>(null, message);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
            new ResponseEntity<>(resErrorMessage, HttpStatus.BAD_REQUEST);

        assertEquals(response, targetedCompanyOfferController
                .getCompanyOffersByStudentOffer(student, 3L));
    }

    @Test
    void getTargetedByStudentTest() {
        Mockito.when(targetedCompanyOfferService.getAllByStudent(student))
            .thenReturn(List.of(targetedCompanyOfferTwo));

        Response<List<TargetedCompanyOffer>> res
                = new Response<>(List.of(targetedCompanyOfferTwo), null);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response
                = targetedCompanyOfferController
            .getAllByStudent(student, studentRole);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(res, response.getBody());
    }

    @Test
    void getTargetedByStudentTestError() {
        String errorMessage = "Such student has not been"
                + " targeted by company offers!";
        Mockito.when(targetedCompanyOfferService.getAllByStudent(student))
                .thenThrow(new IllegalArgumentException(errorMessage));

        Response<List<TargetedCompanyOffer>> res
                = new Response<>(null, errorMessage);
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response
                = targetedCompanyOfferController
                .getAllByStudent(student, studentRole);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(res, response.getBody());
    }

    @Test
    void saveTargetedCompanyOfferUnauthenticatedTest() {
        ResponseEntity<Response<Offer>> response = targetedCompanyOfferController
                .saveTargetedCompanyOffer("", "", targetedCompanyOffer, 3L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(authenticationError,
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void saveTargetedCompanyOfferNotAuthorTest() {
        ResponseEntity<Response<Offer>> response = targetedCompanyOfferController
                .saveTargetedCompanyOffer("fake", companyRole, targetedCompanyOffer, 3L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User not allowed to post this TargetedCompanyOffer",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void saveTargetedCompanyOfferNotCompanyTest() {
        ResponseEntity<Response<Offer>> response = targetedCompanyOfferController
                .saveTargetedCompanyOffer(student, studentRole, targetedCompanyOffer, 3L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User not allowed to post this TargetedCompanyOffer",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getCompanyOffersByIdNotAuthenticatedTest() {
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getCompanyOffersById("", companyRole);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(authenticationError,
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getCompanyOffersByIdNotCompanyTest() {
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getCompanyOffersById("test", studentRole);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is not a company",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getCompanyOffersByStudentOfferUnauthenticatedTest() {
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getCompanyOffersByStudentOffer("", 3L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(authenticationError,
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getCompanyOffersByStudentOfferNotAuthorTest() {
        Mockito.when(targetedCompanyOfferService
                        .getOffersByStudentOffer(3L, student))
                .thenThrow(new UserNotAuthorException(student));
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getCompanyOffersByStudentOffer(student, 3L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User with id Student is not the author of this offer",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getAllByStudentUnauthenticatedTest() {
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getAllByStudent("", studentRole);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(authenticationError,
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void getAllByStudentNotStudentTest() {
        ResponseEntity<Response<List<TargetedCompanyOffer>>> response =
                targetedCompanyOfferController
                        .getAllByStudent(company, companyRole);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is not a student",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

}