package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.OfferRepository;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
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

    private transient StudentOffer studentOffer;
    private transient TargetedCompanyOffer targetedCompanyOffer;
    private transient List<String> expertise;

    //    @BeforeEach
    //    void setup() {
    //        expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
    //        String studentId = "Student";
    //        studentOffer = new StudentOffer("This is a title", "This is a description", 20, 520,
    //            expertise, Status.DISABLED,
    //            32, studentId);
    //
    //        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
    //            "This is a company description",
    //            20, 520, expertise, Status.DISABLED,
    //            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
    //            "Company", null);
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOfferValidTest() {
    //        TargetedCompanyOffer targetedCompanyOffer2 = new TargetedCompanyOffer(
    //            "This is a company title", "This is a company description",
    //            20, 520, expertise, Status.DISABLED,
    //            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
    //            "Company", studentOffer);
    //        studentOffer.setId(33L);
    //        Mockito.when(studentOfferRepository.getById(33L))
    //            .thenReturn(studentOffer);
    //        Mockito.when(offerRepository.save(targetedCompanyOffer))
    //            .thenReturn(targetedCompanyOffer2);
    //
    //        assertEquals(targetedCompanyOffer2, targetedCompanyOfferService
    //            .saveOffer(targetedCompanyOffer, 33L));
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOfferStudentTest() {
    //        targetedCompanyOffer = Mockito.mock(TargetedCompanyOffer.class);
    //        Mockito.when(studentOfferRepository.getById(33L))
    //            .thenReturn(studentOffer);
    //        targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L);
    //        Mockito.verify(targetedCompanyOffer).setStudentOffer(studentOffer);
    //    }
    //
    //    @Test
    //    void saveTargetedCompanyOfferInvalid() {
    //        String errorMessage = "Student offer does not exist";
    //        Mockito.when(studentOfferRepository.getById(33L))
    //            .thenReturn(null);
    //
    //        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
    //            () -> targetedCompanyOfferService.saveOffer(targetedCompanyOffer, 33L));
    //
    //        assertEquals(errorMessage, exception.getMessage());
    //    }

}