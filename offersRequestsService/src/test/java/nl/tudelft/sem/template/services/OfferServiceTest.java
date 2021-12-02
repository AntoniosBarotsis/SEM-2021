//package nl.tudelft.sem.template.services;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import nl.tudelft.sem.template.entities.Offer;
//import nl.tudelft.sem.template.entities.StudentOffer;
//import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
//import nl.tudelft.sem.template.enums.Status;
//import nl.tudelft.sem.template.repositories.OfferRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class OfferServiceTest {
//
//    @Autowired
//    private transient OfferService offerService;
//
//    @MockBean
//    private transient OfferRepository offerRepository;
//
//    private transient StudentOffer studentOffer;
//    private transient TargetedCompanyOffer targetedCompanyOffer;
//    private transient String student;
//
//    @BeforeEach
//    void setup() {
//        student = "Student";
//        List<String> expertise = Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3");
//        studentOffer = new StudentOffer("This is a title", "This is a description",
//            20, 520,
//            Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.DISABLED,
//            32, student);
//        targetedCompanyOffer = new TargetedCompanyOffer("This is a company title",
//            "This is a company description",
//            20, 520, expertise, Status.DISABLED,
//            Arrays.asList("Requirement 1", "Requirement 2", "Requirement 3"),
//            "Company", null);
//    }
//
//    @Test
//    void saveOfferValidTest() {
//        Mockito.when(offerRepository.save(studentOffer))
//            .thenReturn(studentOffer);
//
//        assertEquals(studentOffer, offerService.saveOffer(studentOffer));
//    }
//
//    @Test
//    void saveOfferPendingTest() {
//        studentOffer = Mockito.mock(StudentOffer.class);
//        offerService.saveOffer(studentOffer);
//        Mockito.verify(studentOffer).setStatus(Status.PENDING);
//    }
//
//    @Test
//    void saveOfferTooManyHoursTest() {
//        studentOffer.setHoursPerWeek(21);
//        String errorMessage = "Offer exceeds 20 hours per week";
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> offerService.saveOffer(studentOffer));
//        assertEquals(errorMessage, exception.getMessage());
//    }
//
//    @Test
//    void saveOfferTooLongDurationTest() {
//        studentOffer.setTotalHours(521);
//        String errorMessage = "Offer exceeds 6 month duration";
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> offerService.saveOffer(studentOffer), errorMessage);
//        assertEquals(errorMessage, exception.getMessage());
//    }
//
//    @Test
//    void getAllByUsernameTest() {
//        StudentOffer studentOffer2 = new StudentOffer("This is a second student offer title",
//            "This is a second student offer description",
//            20, 520,
//            Arrays.asList("Expertise 1", "Expertise 2", "Expertise 3"), Status.DISABLED,
//            32, student);
//        targetedCompanyOffer.setStudentOffer(studentOffer);
//        Mockito.when(offerRepository.getAllByUsername(student))
//            .thenReturn(Arrays.asList(studentOffer, studentOffer2, targetedCompanyOffer));
//        Map<String, List<Offer>> expected = new HashMap<>();
//        expected.put("studentOffers", Arrays.asList(studentOffer, studentOffer2));
//        expected.put("targetedCompanyOffers", List.of(targetedCompanyOffer));
//
//        assertEquals(expected, offerService.getAllByUsername(student));
//    }
//
//}