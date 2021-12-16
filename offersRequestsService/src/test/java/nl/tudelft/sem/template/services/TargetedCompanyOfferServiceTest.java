package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logger.FileLogger;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.repositories.OfferRepository;
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
class TargetedCompanyOfferServiceTest {

    @Autowired
    private transient TargetedCompanyOfferService targetedCompanyOfferService;

    @MockBean
    private transient StudentOfferRepository studentOfferRepository;

    @MockBean
    private transient OfferRepository offerRepository;

    @MockBean
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

    @MockBean
    private transient FileLogger fileLogger;

    private transient StudentOffer studentOffer;
    private transient TargetedCompanyOffer targetedCompanyOffer;
    private transient List<String> expertise;
    private transient String student;
    private transient String company;
    private transient String companyRole;
    private transient String studentRole;

    @BeforeEach
    void setup() {
        expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
        student = "Student";
        company = "Company";
        studentRole = "STUDENT";
        companyRole = "COMPANY";
        studentOffer = new StudentOffer("This is a title", "This is a description", 20, 520,
            expertise, Status.DISABLED,
            32, student);

        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
            "This is a company description",
            20, 520, expertise, Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            "Company", null);
    }

    @Test
    void saveTargetedCompanyOfferValidTest() {
        TargetedCompanyOffer targetedCompanyOffer2 = new TargetedCompanyOffer(
            "This is a company title", "This is a company description",
            20, 520, expertise, Status.DISABLED,
            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
            "Company", studentOffer);
        studentOffer.setId(33L);
        Mockito.when(studentOfferRepository.getById(33L))
            .thenReturn(studentOffer);
        Mockito.when(offerRepository.save(targetedCompanyOffer))
            .thenReturn(targetedCompanyOffer2);

        assertEquals(targetedCompanyOffer2, targetedCompanyOfferService
            .saveOffer(targetedCompanyOffer, 33L));
    }

    @Test
    void saveTargetedCompanyOfferStudentTest() {
        targetedCompanyOffer = Mockito.mock(TargetedCompanyOffer.class);
        Mockito.when(studentOfferRepository.getById(33L))
            .thenReturn(studentOffer);
        Mockito.when(offerRepository.save(targetedCompanyOffer))
                        .thenReturn(targetedCompanyOffer);
        targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L);
        Mockito.verify(targetedCompanyOffer).setStudentOffer(studentOffer);
    }

    @Test
    void saveTargetedCompanyOfferInvalid() {
        String errorMessage = "Student offer does not exist";
        Mockito.when(studentOfferRepository.getById(33L))
            .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void getOffersByIdTestFail() {
        Mockito.when(targetedCompanyOfferRepository.findAllByCompanyId(any()))
            .thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> targetedCompanyOfferService.getOffersById("CompanyOne"));
        String message = "No such company has made offers!";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void getOffersByIdTestPass() {
        List<Offer> returned = new ArrayList<>();
        returned.add(targetedCompanyOffer);
        Mockito.when(targetedCompanyOfferRepository.findAllByCompanyId("MyCompany"))
            .thenReturn(returned);

        assertEquals(returned, targetedCompanyOfferService.getOffersById("MyCompany"));
    }

    @Test
    void getOffersByStudentTestPass() {
        List<Offer> returned = new ArrayList<>();
        returned.add(targetedCompanyOffer);

        Mockito.when(studentOfferRepository.getById(any()))
            .thenReturn(studentOffer);

        Mockito.when(targetedCompanyOfferRepository.findAllByStudentOffer(studentOffer))
            .thenReturn(returned);

        assertEquals(returned,
            targetedCompanyOfferService.getOffersByStudentOffer(studentOffer.getId(), student));
    }

    @Test
    void getOffersByStudentTestFailNull() {
        Mockito.when(studentOfferRepository.getById(any()))
            .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> targetedCompanyOfferService
                .getOffersByStudentOffer(studentOffer.getId(), student));
        String message = "Student offer does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void getTargetedByStudentTest() {
        Mockito.when(targetedCompanyOfferRepository.getAllByStudent("Student"))
            .thenReturn(List.of(targetedCompanyOffer));
        List<TargetedCompanyOffer> result = targetedCompanyOfferService
            .getAllByStudent("Student");
        assertEquals(List.of(targetedCompanyOffer), result);

    }

    @Test
    void getOffersByStudentOfferNotAuthorTest() {
        Mockito.when(studentOfferRepository.getById(3L))
                .thenReturn(studentOffer);
        UserNotAuthorException exception = assertThrows(UserNotAuthorException.class,
                () -> targetedCompanyOfferService
                        .getOffersByStudentOffer(3L, "fake"));
        String message = "User with id fake is not the author of this offer";
        assertEquals(message, exception.getMessage());
    }

}