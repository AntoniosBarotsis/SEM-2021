package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.services.OfferService;
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
class OfferControllerTest {

    @Autowired
    private transient OfferController offerController;

    @MockBean
    private transient OfferService offerService;

    private transient StudentOffer studentOffer;
    private transient TargetedCompanyOffer targetedCompanyOffer;

    //    @BeforeEach
    //    void setup() {
    //        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
    //        String studentId = "Student";
    //        studentOffer = new StudentOffer("This is a title", "This is a description", 20, 520,
    //            expertise, Status.DISABLED,
    //            32, studentId);
    //        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
    //            "This is a company description",
    //            20, 520, expertise, Status.DISABLED,
    //            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
    //            "Company", studentId);
    //    }
    //
    //    @Test
    //    void saveStudentOfferValid() {
    //        Offer studentOffer2 = new StudentOffer("This is a title", "This is a description", 20,
    //            520, Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.PENDING,
    //            32, "Student");
    //        Mockito.when(offerService.saveOffer(studentOffer))
    //            .thenReturn(studentOffer2);
    //
    //        ResponseEntity<?> response = offerController.saveStudentOffer(studentOffer);
    //        assertEquals(studentOffer2, response.getBody());
    //        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveStudentOfferTooManyHours() {
    //        studentOffer.setHoursPerWeek(21);
    //        String errorMessage = "Offer exceeds 20 hours per week";
    //        Mockito.when(offerService.saveOffer(studentOffer))
    //            .thenThrow(new IllegalArgumentException(errorMessage));
    //
    //        ResponseEntity<?> response = offerController.saveStudentOffer(studentOffer);
    //        assertEquals(errorMessage, response.getBody());
    //        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveStudentOfferTooLongDuration() {
    //        studentOffer.setTotalHours(521);
    //        String errorMessage = "Offer exceeds 6 month duration";
    //        Mockito.when(offerService.saveOffer(studentOffer))
    //            .thenThrow(new IllegalArgumentException(errorMessage));
    //
    //        ResponseEntity<?> response = offerController.saveStudentOffer(studentOffer);
    //        assertEquals(errorMessage, response.getBody());
    //        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOfferValid() {
    //        TargetedCompanyOffer targetedCompanyOffer2 = new TargetedCompanyOffer(
    //            "This is a company title", "This is a company description",
    //            20, 520,
    //            Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.DISABLED,
    //            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
    //            "Company", "Student");
    //        Mockito.when(offerService.saveOffer(targetedCompanyOffer))
    //            .thenReturn(targetedCompanyOffer2);
    //
    //        ResponseEntity<?> response = offerController
    //            .saveTargetedCompanyOffer(targetedCompanyOffer);
    //        assertEquals(targetedCompanyOffer2, response.getBody());
    //        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOfferTooManyHours() {
    //        targetedCompanyOffer.setHoursPerWeek(21);
    //        String errorMessage = "Offer exceeds 20 hours per week";
    //        Mockito.when(offerService.saveOffer(targetedCompanyOffer))
    //            .thenThrow(new IllegalArgumentException(errorMessage));
    //
    //        ResponseEntity<?> response = offerController
    //            .saveTargetedCompanyOffer(targetedCompanyOffer);
    //        assertEquals(errorMessage, response.getBody());
    //        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOffer() {
    //        targetedCompanyOffer.setTotalHours(521);
    //        String errorMessage = "Offer exceeds 6 month duration";
    //        Mockito.when(offerService.saveOffer(targetedCompanyOffer))
    //            .thenThrow(new IllegalArgumentException(errorMessage));
    //
    //        ResponseEntity<?> response = offerController
    //            .saveTargetedCompanyOffer(targetedCompanyOffer);
    //        assertEquals(errorMessage, response.getBody());
    //        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    }
}