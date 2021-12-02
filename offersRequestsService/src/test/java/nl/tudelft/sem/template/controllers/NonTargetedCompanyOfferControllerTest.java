//package nl.tudelft.sem.template.controllers;
//
//import nl.tudelft.sem.template.entities.Application;
//import nl.tudelft.sem.template.entities.NonTargetedCompanyOffer;
//import nl.tudelft.sem.template.entities.Offer;
//import nl.tudelft.sem.template.responses.Response;
//import nl.tudelft.sem.template.services.NonTargetedCompanyOfferService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class NonTargetedCompanyOfferControllerTest {
//
//    @Autowired
//    private transient NonTargetedCompanyOfferController offerController;
//
//    @MockBean
//    private transient NonTargetedCompanyOfferService offerService;
//
//    private transient NonTargetedCompanyOffer offer;
//    private transient Application application;
//
//
//    @BeforeEach
//    void setup() {
//        List<String> expertise = List.of("e1", "e2", "e3");
//        List<String> requirements = List.of("r1", "r2", "r3");
//        offer = new NonTargetedCompanyOffer("title", "description",
//                20, 520, expertise,
//                null, requirements, "facebook");
//        application = new Application("student", 5, offer);
//    }
//
//    @Test
//    void saveOfferNotAuthenticatedTest() {
//        String errorMessage = "User has not been authenticated";
//        ResponseEntity<Response<Offer>> response = offerController
//                .createNonTargetedCompanyOffer(offer, "", "");
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void saveOfferNotSameAuthorTest() {
//        String errorMessage = "User can not make this offer";
//        ResponseEntity<Response<Offer>> response = offerController
//                .createNonTargetedCompanyOffer(offer, "google", "COMPANY");
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void saveOfferNotCompanyTest() {
//        String errorMessage = "User can not make this offer";
//        ResponseEntity<Response<Offer>> response = offerController
//                .createNonTargetedCompanyOffer(offer, "facebook", "STUDENT");
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void saveOfferValidTest() {
//        Mockito.when(offerService.saveOffer(offer))
//                .thenReturn(offer);
//        ResponseEntity<Response<Offer>> response = offerController
//                .createNonTargetedCompanyOffer(offer, "facebook", "COMPANY");
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(new Response<>(offer), response.getBody());
//    }
//
//    @Test
//    void saveOfferIllegalTest() {
//        String error = "error";
//        Mockito.when(offerService.saveOffer(offer))
//                .thenThrow(new IllegalArgumentException(error));
//
//        ResponseEntity<Response<Offer>> response = offerController
//                .createNonTargetedCompanyOffer(offer, "facebook", "COMPANY");
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("error", response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void applyNotAuthenticatedTest() {
//        String errorMessage = "User has not been authenticated";
//        ResponseEntity<Response<Application>> response = offerController
//                .apply("", "", application, 3L);
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void applyNotAuthorTest() {
//        String errorMessage = "User can not make this application";
//        ResponseEntity<Response<Application>> response = offerController
//                .apply("fake", "STUDENT", application, 3L);
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void applyNotStudentTest() {
//        String errorMessage = "User can not make this application";
//        ResponseEntity<Response<Application>> response = offerController
//                .apply("student", "COMPANY", application, 3L);
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertEquals(errorMessage, response.getBody().getErrorMessage());
//    }
//
//    @Test
//    void applyValidTest() {
//        Mockito.when(offerService.apply(application, 3L))
//                .thenReturn(application);
//        ResponseEntity<Response<Application>> response = offerController
//                .apply("student", "STUDENT", application, 3L);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(new Response<>(application), response.getBody());
//    }
//
//    @Test
//    void applyIllegalTest() {
//        String error = "error";
//        Mockito.when(offerService.apply(application, 3L))
//                .thenThrow(new IllegalArgumentException(error));
//
//        ResponseEntity<Response<Application>> response = offerController
//                .apply("student", "STUDENT", application, 3L);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals(error, response.getBody().getErrorMessage());
//    }
//
//
//}