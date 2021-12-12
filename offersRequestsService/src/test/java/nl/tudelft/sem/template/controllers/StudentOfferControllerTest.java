package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.naming.NoPermissionException;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.entities.dtos.ContractDto;
import nl.tudelft.sem.template.entities.dtos.Response;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.ContractCreationException;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import nl.tudelft.sem.template.services.StudentOfferService;
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
class StudentOfferControllerTest {

    @Autowired
    private transient StudentOfferController studentOfferController;

    @MockBean
    private transient StudentOfferService studentOfferService;

    private transient StudentOffer studentOffer;
    private transient String student;
    private transient String studentRole;
    private transient TargetedCompanyOffer targetedCompanyOffer;
    private transient ContractDto contract;

    @BeforeEach
    void setup() {
        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        student = "Student";
        studentRole = "STUDENT";
        studentOffer = new StudentOffer("This is a title",
            "This is a description", 20, 520,
            expertise, Status.DISABLED,
            32, student);
        targetedCompanyOffer = new TargetedCompanyOffer("Ben's services", "Hey I'm Ben",
                15, 150,
                Arrays.asList("Singing", "Web Dev", "Care-taking"), Status.PENDING,
                Arrays.asList("Singing", "Web Dev", "Care-taking"),
                "Our Company", studentOffer);

        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = startDate.plusWeeks(
                (long) Math.ceil(studentOffer.getTotalHours() / studentOffer.getHoursPerWeek()));

        contract = new ContractDto(1L, targetedCompanyOffer.getCompanyId(), student,
                startDate, endDate, targetedCompanyOffer.getHoursPerWeek(),
                targetedCompanyOffer.getTotalHours(),
                studentOffer.getPricePerHour(), "ACTIVE");
    }


    @Test
    void saveStudentOfferValid() {
        Offer studentOffer2 = new StudentOffer("This is a title",
            "This is a description", 20,
            520, Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.PENDING,
            32, "Student");
        Mockito.when(studentOfferService.saveOffer(studentOffer))
            .thenReturn(studentOffer2);

        Response<Offer> res = new Response<>(studentOffer2, null);

        ResponseEntity<Response<Offer>> response
                = studentOfferController.saveStudentOffer(student, studentRole, studentOffer);
        assertEquals(res, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void saveStudentOfferIllegal() {
        studentOffer.setHoursPerWeek(21);
        String errorMessage = "Offer exceeds 20 hours per week";
        Mockito.when(studentOfferService.saveOffer(studentOffer))
            .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<Response<Offer>> response
                = studentOfferController.saveStudentOffer(student, studentRole, studentOffer);

        Response<Offer> errorResponse = new Response<>(null, errorMessage);
        assertEquals(errorResponse, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllStudentOffersTest() {
        List<StudentOffer> list = List.of(studentOffer);
        Mockito.when(studentOfferService.getOffers())
            .thenReturn(list);

        ResponseEntity<Response<List<StudentOffer>>> response
                = studentOfferController.getAllStudentOffers();
        Response<List<StudentOffer>> offersRespond =
                new Response<>(list, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offersRespond, response.getBody());
    }

    @Test
    void getStudentOffersByIdValidTest() {
        List<StudentOffer> studentOffers = List.of(studentOffer);
        Mockito.when(studentOfferService.getOffersById(student))
            .thenReturn(studentOffers);

        ResponseEntity<Response<List<StudentOffer>>> response
                = studentOfferController.getStudentOffersById(student);
        Response<List<StudentOffer>> offersRespond =
                new Response<>(studentOffers, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offersRespond, response.getBody());
    }

    @Test
    void getStudentOffersByIdIllegalTest() {
        String errorMessage = "Error";
        Mockito.when(studentOfferService.getOffersById(student))
            .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<Response<List<StudentOffer>>> response
                = studentOfferController.getStudentOffersById(student);
        Response<List<StudentOffer>> responseError =
                new Response<>(null, errorMessage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(responseError, response.getBody());
    }

    @Test
    void editStudentOfferTest() {
        Mockito.doNothing()
                .when(studentOfferService)
                .updateStudentOffer(studentOffer);

        ResponseEntity<Response<String>> res
                = studentOfferController.editStudentOffer(studentOffer, student, studentRole);
        Response<String> response =
                new Response<>("Student Offer has been updated successfully!", null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void editStudentOfferTestFailUserName() {
        student = "";
        ResponseEntity<Response<String>> res
                = studentOfferController.editStudentOffer(studentOffer, student, studentRole);
        Response<String> response =
                new Response<>(null, "User has not been authenticated");

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void editStudentOfferTestFailRole() {
        studentRole = "COMPANY";
        ResponseEntity<Response<String>> res
                = studentOfferController.editStudentOffer(studentOffer, student, studentRole);
        Response<String> response =
                new Response<>(null, "User is not allowed to edit this offer");

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void editStudentOfferTestFailIllegalArgument() {
        Mockito
                .doThrow(new IllegalArgumentException("You are not allowed to edit the Status"))
                .when(studentOfferService).updateStudentOffer(studentOffer);

        ResponseEntity<Response<String>> res
                = studentOfferController.editStudentOffer(studentOffer, student, studentRole);
        Response<String> response =
                new Response<>(null, "You are not allowed to edit the Status");

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void saveStudentOfferUnauthenticatedTest() {
        ResponseEntity<Response<Offer>> response = studentOfferController
                .saveStudentOffer("", "", studentOffer);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User is not authenticated",
                Objects.requireNonNull(response.getBody()).getErrorMessage());

    }

    @Test
    void saveStudentOfferNotAuthorTest() {
        ResponseEntity<Response<Offer>> response = studentOfferController
                .saveStudentOffer("fake", "STUDENT", studentOffer);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User not allowed to post this StudentOffer",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void saveStudentOfferNotStudentTest() {
        ResponseEntity<Response<Offer>> response = studentOfferController
                .saveStudentOffer("fake", "COMPANY", studentOffer);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User not allowed to post this StudentOffer",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    void acceptTargetedOfferTest() throws NoPermissionException, ContractCreationException {
        Mockito.when(studentOfferService
                .acceptOffer(student, studentRole, targetedCompanyOffer.getId()))
                .thenReturn(contract);

        ResponseEntity<Response<ContractDto>> res
                = studentOfferController
                .acceptTargetedOffer(student, studentRole, targetedCompanyOffer.getId());
        Response<ContractDto> response =
                new Response<>(contract, "The Company Offer was accepted successfully!");

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void acceptTargetedOfferTestFailUserName() {
        student = "";
        ResponseEntity<Response<ContractDto>> res
                = studentOfferController
                .acceptTargetedOffer(student, studentRole, targetedCompanyOffer.getId());
        Response<ContractDto> response =
                new Response<>(null, "User has not been authenticated");

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void acceptTargetedOfferTestFailIllegalArgument() throws NoPermissionException, ContractCreationException {
        String message = "The StudentOffer or TargetedRequest is not active anymore!";
        Mockito
                .doThrow(new IllegalArgumentException(
                        message))
                .when(studentOfferService)
                .acceptOffer(student, studentRole, targetedCompanyOffer.getId());

        ResponseEntity<Response<ContractDto>> res
                = studentOfferController
                .acceptTargetedOffer(student, studentRole, targetedCompanyOffer.getId());
        Response<ContractDto> response =
                new Response<>(null, message);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void acceptTargetedOfferTestFailNoPermission() throws NoPermissionException, ContractCreationException {
        studentRole = "COMPANY";
        String message = "User not allowed to accept this TargetedOffer";
        Mockito
                .doThrow(new NoPermissionException(
                        message))
                .when(studentOfferService)
                .acceptOffer(student, studentRole, targetedCompanyOffer.getId());

        ResponseEntity<Response<ContractDto>> res
                = studentOfferController
                .acceptTargetedOffer(student, studentRole, targetedCompanyOffer.getId());
        Response<ContractDto> response =
                new Response<>(null, message);

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
        assertEquals(response, res.getBody());
    }

    @Test
    void getStudentOffersByIdUnavailableTest() {
        Mockito.when(studentOfferService.getOffersById(student))
                .thenThrow(new UserServiceUnvanvailableException("test"));

        ResponseEntity<Response<List<StudentOffer>>> response =
                studentOfferController.getStudentOffersById(student);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("test", response.getBody().getErrorMessage());
    }

    @Test
    void getStudentOffersByIdUserNotExistTest() {
        Mockito.when(studentOfferService.getOffersById(student))
                .thenThrow(new UserDoesNotExistException("error"));

        ResponseEntity<Response<List<StudentOffer>>> response =
                studentOfferController.getStudentOffersById(student);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getErrorMessage());
    }

}