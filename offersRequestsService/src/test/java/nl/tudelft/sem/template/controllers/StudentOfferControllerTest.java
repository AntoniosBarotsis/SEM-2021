package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.dtos.Response;
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
    private transient String student;

    @BeforeEach
    void setup() {
        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        student = "Student";
        studentOffer = new StudentOffer("This is a title",
            "This is a description", 20, 520,
            expertise, Status.DISABLED,
            32, student);
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
                = studentOfferController.saveStudentOffer(studentOffer);
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
                = studentOfferController.saveStudentOffer(studentOffer);

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

}