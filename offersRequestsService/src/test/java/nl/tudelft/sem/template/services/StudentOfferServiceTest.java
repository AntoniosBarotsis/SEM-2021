package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentOfferServiceTest {

    @Autowired
    private transient StudentOfferService studentOfferService;

    @MockBean
    private transient StudentOfferRepository studentOfferRepository;
    @MockBean
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

    private transient StudentOffer offerTwo;
    private transient StudentOffer offerThree;
    private transient String student;
    private transient TargetedCompanyOffer accepted;

    @BeforeEach
    void setUp() {
        student = "Student";
        offerTwo = new StudentOffer("Rado's services", "Hey I'm Rado",
            10, 100,
            Arrays.asList("Drawing", "Swimming", "Running"),
            Status.PENDING,
            50, "0123454");
        offerThree = new StudentOffer("Ben's services", "Hey I'm Ben",
            15, 150,
            Arrays.asList("Singing", "Web Dev", "Care-taking"), Status.ACCEPTED,
            50, student);
        accepted = new TargetedCompanyOffer("Ben's services", "Hey I'm Ben",
                15, 150,
                Arrays.asList("Singing", "Web Dev", "Care-taking"), Status.PENDING,
                Arrays.asList("Singing", "Web Dev", "Care-taking"),
                "Our Company", offerTwo);
    }

    @Test
    void getOffersTest() {
        List<StudentOffer> returned = new ArrayList<>();
        returned.add(offerTwo);


        Mockito.when(studentOfferRepository.findAllActive())
                .thenReturn(returned);

        assertEquals(returned, studentOfferService.getOffers());
    }

    @Test
    void getOffersByIdTestPass() {
        List<StudentOffer> returned = new ArrayList<>();
        returned.add(offerThree);

        Mockito.when(studentOfferRepository.findAllByStudentId(student))
                .thenReturn(returned);

        assertEquals(returned, studentOfferService.getOffersById(student));
    }

    @Test
    void getOffersByIdTestFailEmpty() {
        Mockito.when(studentOfferRepository.findAllByStudentId(student))
                .thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentOfferService.getOffersById(student));
        String message = "No such student has made offers!";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void acceptOfferTest() {
        TargetedCompanyOffer declined = new TargetedCompanyOffer();
        offerTwo.setTargetedCompanyOffers(List.of(declined, accepted));

        Mockito.when(studentOfferRepository.getById(offerTwo.getId()))
                .thenReturn(offerTwo);

        studentOfferService.acceptOffer(accepted);

        Mockito.verify(studentOfferRepository, times(1)).save(any());
        Mockito.verify(targetedCompanyOfferRepository, times(2)).save(any());
        assertSame(accepted.getStatus(), Status.ACCEPTED);
        assertSame(offerTwo.getStatus(), Status.DISABLED);
        assertSame(declined.getStatus(), Status.DECLINED);
    }

    @Test
    void acceptOfferTestFailInvalid() {
        Mockito.when(studentOfferRepository.getById(offerTwo.getId()))
                .thenReturn(null);

        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class,
                    () -> studentOfferService.acceptOffer(accepted));
        String errorMessage = "Offer is not valid!";
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void acceptOfferTestFailNotContained() {
        offerTwo.setTargetedCompanyOffers(new ArrayList<>());
        Mockito.when(studentOfferRepository.getById(offerTwo.getId()))
                .thenReturn(offerTwo);

        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class,
                    () -> studentOfferService.acceptOffer(accepted));
        String errorMessage = "Student Offer does not contain this Targeted Offer";
        assertEquals(errorMessage, exception.getMessage());
    }
}
