package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.enums.Status;
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

    //    @BeforeEach
    //    void setup() {
    //        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
    //        String studentId = "Student";
    //        studentOffer = new StudentOffer("This is a title",
    //            "This is a description", 20, 520,
    //            expertise, Status.DISABLED,
    //            32, studentId);
    //    }
    //
    //
    //    @Test
    //    void saveStudentOfferValid() {
    //        Offer studentOffer2 = new StudentOffer("This is a title",
    //            "This is a description", 20,
    //            520, Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.PENDING,
    //            32, "Student");
    //        Mockito.when(studentOfferService.saveOffer(studentOffer))
    //            .thenReturn(studentOffer2);
    //
    //        ResponseEntity<?> response = studentOfferController.saveStudentOffer(studentOffer);
    //        assertEquals(studentOffer2, response.getBody());
    //        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //    }
    //
    //    @Test
    //    void saveStudentOfferIllegal() {
    //        studentOffer.setHoursPerWeek(21);
    //        String errorMessage = "Offer exceeds 20 hours per week";
    //        Mockito.when(studentOfferService.saveOffer(studentOffer))
    //            .thenThrow(new IllegalArgumentException(errorMessage));
    //
    //        ResponseEntity<?> response = studentOfferController.saveStudentOffer(studentOffer);
    //        assertEquals(errorMessage, response.getBody());
    //        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    }

}