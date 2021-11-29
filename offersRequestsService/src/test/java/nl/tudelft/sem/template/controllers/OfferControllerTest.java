package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.responses.Response;
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

    @BeforeEach
    void setup() {
        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        String studentId = "Student";
        studentOffer = new StudentOffer("This is a title",
            "This is a description", 20, 520,
            expertise, Status.DISABLED,
            32, studentId);
        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
            "This is a company description",
            20, 520, expertise, Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            "Company", null);
    }

    @Test
    void getAllByUsernameTest() {
        targetedCompanyOffer.setStudentOffer(studentOffer);
        Map<String, List<Offer>> expectedMap = new HashMap<>();
        expectedMap.put("studentOffers", List.of(studentOffer));
        expectedMap.put("targetedCompanyOffers", List.of(targetedCompanyOffer));
        Mockito.when(offerService.getAllByUsername("Student"))
            .thenReturn(expectedMap);

        ResponseEntity<Response<Map<String, List<Offer>>>> response
                = offerController
            .getAllByUsername("Student");
        Response<Map<String, List<Offer>>> res
                = new Response<>(expectedMap, null);

        assertEquals(res, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}