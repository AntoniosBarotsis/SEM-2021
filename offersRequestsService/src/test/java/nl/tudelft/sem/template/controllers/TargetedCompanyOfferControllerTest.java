package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
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
    /*
    @BeforeEach
    void setup() {
        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        String studentId = "Student";
        studentOffer = new StudentOffer("This is a title", "This is a description",
                20, 520,
            expertise, Status.DISABLED,
            32, studentId);
        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
           "This is a company description",
            20, 520, expertise, Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            "Company", null);
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

        ResponseEntity<?> response = targetedCompanyOfferController
            .saveTargetedCompanyOffer(targetedCompanyOffer, 33L);
        assertEquals(targetedCompanyOffer2, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void saveTargetedCompanyOfferIllegal() {
        targetedCompanyOffer.setHoursPerWeek(21);
        String errorMessage = "Offer exceeds 20 hours per week";
        Mockito.when(targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L))
            .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = targetedCompanyOfferController
            .saveTargetedCompanyOffer(targetedCompanyOffer, 33L);
        assertEquals(errorMessage, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCompanyOffersByIdTextPass() {
        List<TargetedCompanyOffer> returned = new ArrayList<>();
        returned.add(targetedCompanyOfferTwo);

        Mockito.when(targetedCompanyOfferService.getOffersById("MoneyNL"))
                .thenReturn(returned);

        ResponseEntity<List<TargetedCompanyOffer>> response =
                new ResponseEntity<>(returned, HttpStatus.OK);

        assertEquals(response, targetedCompanyOfferController.getCompanyOffersById("MoneyNL"));
    }

    @Test
    void getCompanyOffersByIdTextFail() {
        String message = "No such company has made offers!";

        Mockito.when(targetedCompanyOfferService.getOffersById(any()))
                .thenThrow(new IllegalArgumentException(message));

        ResponseEntity<String> response =
                new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        assertEquals(response, targetedCompanyOfferController.getCompanyOffersById("Company"));
    }

    @Test
    void getCompanyOffersByStudentOfferTestPass() {
        List<TargetedCompanyOffer> returned = new ArrayList<>();
        returned.add(targetedCompanyOfferTwo);

        Long studentOfferId = targetedCompanyOfferTwo
                .getStudentOffer().getId();

        Mockito.when(targetedCompanyOfferService
                        .getOffersByStudentOffer(studentOfferId))
                .thenReturn(returned);

        ResponseEntity<List<TargetedCompanyOffer>> response =
                new ResponseEntity<>(returned, HttpStatus.OK);

        assertEquals(response,
                targetedCompanyOfferController
                        .getCompanyOffersByStudentOffer(studentOfferId));
    }

    @Test
    void getCompanyOffersByStudentOfferTestFail() {
        String message = "Student offer does not exist";

        Mockito.when(targetedCompanyOfferService.getOffersByStudentOffer(any()))
                .thenThrow(new IllegalArgumentException(message));

        ResponseEntity<String> response =
            new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

        assertEquals(response, targetedCompanyOfferController
                .getCompanyOffersByStudentOffer(targetedCompanyOfferTwo
                .getStudentOffer().getId()));
    }
    */
}