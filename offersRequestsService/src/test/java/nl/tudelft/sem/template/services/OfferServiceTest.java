package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
class OfferServiceTest {

    @Autowired
    private OfferService offerService;

    @MockBean
    private OfferRepository offerRepository;

    StudentOffer studentOffer;

    @BeforeEach
    void setup() {
        studentOffer = new StudentOffer("This is a title", "This is a description", 20, 520,
            Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.DISABLED,
            32, "Student");
    }

    @Test
    void saveOfferValidTest() {
        Mockito.when(offerRepository.save(studentOffer))
            .thenReturn(studentOffer);

        assertEquals(studentOffer, offerService.saveOffer(studentOffer));
    }

    @Test
    void saveOfferPendingTest() {
        studentOffer = Mockito.mock(StudentOffer.class);
        offerService.saveOffer(studentOffer);
        Mockito.verify(studentOffer).setStatus(Status.PENDING);
    }

    @Test
    void saveOfferTooManyHoursTest() {
        studentOffer.setHoursPerWeek(21);
        String errorMessage = "Offer exceeds 20 hours per week";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> offerService.saveOffer(studentOffer));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void saveOfferTooLongDurationTest() {
        studentOffer.setTotalHours(521);
        String errorMessage = "Offer exceeds 6 month duration";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> offerService.saveOffer(studentOffer), errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}