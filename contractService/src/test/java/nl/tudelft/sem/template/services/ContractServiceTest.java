package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import logger.FileLogger;
import nl.tudelft.sem.template.dtos.requests.ContractRequest;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.enums.ContractStatus;
import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidContractException;
import nl.tudelft.sem.template.repositories.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
class ContractServiceTest {

    @Autowired
    private transient ContractService contractService;

    @MockBean
    private transient ContractRepository contractRepository;

    @MockBean
    private transient FileLogger fileLogger;

    private transient Contract contract;
    private transient Contract contractToBeSaved;
    private final transient String companyId = "TUDelft";
    private final transient String studentId = "JohnDoe";

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.of(2021, 12, 25);
        LocalDate endDate = startDate.plusWeeks(3);
        contract = new Contract(1L, companyId, studentId, startDate, endDate, 14,
                42, 15, ContractStatus.ACTIVE, null);

        // Contract passed with the request body:
        ContractRequest contractRequest = new ContractRequest(companyId, studentId, 14d, 42d, 15d);
        contractToBeSaved = contractRequest.toContract();
    }

    @Test
    @Tag("saveContract")
    void saveValidContractSuccess() throws InvalidContractException {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);
        when(contractRepository.save(contractToBeSaved)).thenReturn(contract);

        Contract actual = contractService.saveContract(contractToBeSaved);
        
        // Set contract id to null for the argumentCaptor assertion:
        contract.setId(null);

        // Assert that the contract params are correct before saving:
        ArgumentCaptor<Contract> contractArgumentCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository).save(contractArgumentCaptor.capture());

        // Get current date from the captured contract value:
        LocalDate today = contractArgumentCaptor.getValue().getStartDate();
        contract.setStartDate(today);
        contract.setEndDate(today.plusWeeks(3));

        assertEquals(contract, contractArgumentCaptor.getValue());

        contract.setId(1L);
        assertEquals(contract, actual);

        // File Logger:
        verify(fileLogger).log("Contract " + contract.getId() + " has been saved between company "
                + companyId + " and student " + studentId);
    }

    @Test
    @Tag("saveContract")
    void saveInvalidContractException() {
        // Contract between the same person
        contract.setStudentId(contract.getCompanyId());
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(InvalidContractException.class, () -> contractService.saveContract(contract));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getContractAccessDenied() {
        assertThrows(AccessDeniedException.class,
                () -> contractService.getContract(companyId, studentId, true, "BillGates"));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getActiveContractSuccess() throws ContractNotFoundException, AccessDeniedException {
        // Just to be sure the end date doesn't pass:
        contract.setEndDate(LocalDate.of(4000, 1, 1));

        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(contract);

        assertEquals(contract, contractService.getContract(companyId, studentId, true, studentId));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getActiveContractNotFound() {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(ContractNotFoundException.class,
                () -> contractService.getContract(companyId, studentId, true, studentId));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getActiveContractThatExpired() {
        // The end date passed, so it should be set to expired and throw exception:
        contract.setEndDate(LocalDate.of(2021, 12, 1));

        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(contract);

        assertThrows(ContractNotFoundException.class,
                () -> contractService.getContract(companyId, studentId, true, studentId));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getMostRecentContractSuccess() throws ContractNotFoundException, AccessDeniedException {
        // Just to be sure the end date doesn't pass:
        contract.setEndDate(LocalDate.of(4000, 1, 1));

        when(contractRepository
                .findFirstByCompanyIdEqualsAndStudentIdEqualsOrderByStartDateDesc(companyId, studentId))
                .thenReturn(contract);

        assertEquals(contract, contractService.getContract(companyId, studentId, false, studentId));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getMostRecentContractNotFound() throws ContractNotFoundException, AccessDeniedException {
        when(contractRepository
                .findFirstByCompanyIdEqualsAndStudentIdEqualsOrderByStartDateDesc(companyId, studentId))
                .thenReturn(null);

        assertThrows(ContractNotFoundException.class,
                () -> contractService.getContract(companyId, studentId, false, studentId));
    }

    @Test
    @Tag("getContractByCompanyAndStudent")
    void getMostRecentContractExpired() throws ContractNotFoundException, AccessDeniedException {
        contract.setEndDate(LocalDate.of(2021, 12, 1));
        // Contract still active:
        when(contractRepository
                .findFirstByCompanyIdEqualsAndStudentIdEqualsOrderByStartDateDesc(companyId, studentId))
                .thenReturn(contract);

        Contract actual = contractService.getContract(companyId, studentId, false, studentId);

        // Status should be set to expired:
        contract.setStatus(ContractStatus.EXPIRED);

        assertEquals(contract, actual);
    }

    @Test
    @Tag("getContractById")
    void getContractByIdNotFound() {
        when(contractRepository
                .findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class,
                () -> contractService.getContract(companyId, studentId, true, studentId));
    }

    @Test
    @Tag("getContractById")
    void getContractByIdShouldExpire() throws ContractNotFoundException {
        contract.setEndDate(LocalDate.of(2021, 12, 1));

        when(contractRepository
                .findById(1L)).thenReturn(Optional.of(contract));  // Still active when retrieved

        contract.setStatus(ContractStatus.EXPIRED);
        assertEquals(contract, contractService.getContract(1L));
    }

    @Test
    @Tag("getContractById")
    void getContractByIdShouldNotExpire() throws ContractNotFoundException {
        // Just to be sure the end date doesn't pass:
        contract.setEndDate(LocalDate.of(4000, 1, 1));

        when(contractRepository
                .findById(1L)).thenReturn(Optional.of(contract));

        assertEquals(contract, contractService.getContract(1L));
    }

    @Test
    @Tag("terminateContract")
    void terminateContractSuccess() throws ContractNotFoundException,
            InactiveContractException, AccessDeniedException {
        // Just to be sure the end date doesn't pass:
        contract.setEndDate(LocalDate.of(4000, 1, 1));

        when(contractRepository.findById(contract.getId()))
                .thenReturn(Optional.of(contract));

        contractService.terminateContract(contract.getId(), studentId);

        verify(contractRepository).terminateContract(contract.getId());

        // File Logger:
        verify(fileLogger).log("Contract " + contract.getId() + " has been terminated");
    }

    @Test
    @Tag("terminateContract")
    void terminateContractNotActive() {
        contract.setStatus(ContractStatus.TERMINATED);

        when(contractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));

        assertThrows(InactiveContractException.class,
                () -> contractService.terminateContract(contract.getId(), studentId));
    }

    // -----------------------
    //  HELPER METHODS TESTS:
    // -----------------------

    @Test
    @Tag("ValidateContract")
    void validateContractSuccess() {
        assertDoesNotThrow(() -> contractService.validateContract(contractToBeSaved));
    }

    @Test
    void validateContractSamePartiesFailed() {
        contractToBeSaved.setCompanyId(studentId);

        assertThrows(InvalidContractException.class,
                () -> contractService.validateContract(contractToBeSaved));
    }

    /**
     * ON-POINT (also OUT-POINT) boundary test (max hours per week > 20 throws exception).
     */
    @Test
    @Tag("ValidateContract")
    @Tag("BoundaryTest")
    void validateContractMaxHoursCorrect() {
        contractToBeSaved.setHoursPerWeek(20d);

        assertDoesNotThrow(() -> contractService.validateContract(contractToBeSaved));
    }

    /**
     * OFF-POINT (also an IN-POINT) boundary test (max hours per week > 20 throws exception).
     */
    @Test
    @Tag("ValidateContract")
    @Tag("BoundaryTest")
    void validateContractMaxHoursExceeded() {
        contractToBeSaved.setHoursPerWeek(21d);

        assertThrows(InvalidContractException.class,
                () -> contractService.validateContract(contractToBeSaved));
    }

    /**
     * ON-POINT (also an OUT-POINT) boundary test (total weeks > 26 throws exception).
     */
    @Test
    @Tag("ValidateContract")
    @Tag("BoundaryTest")
    void validateContractTotalWeeksCorrect() {
        contractToBeSaved.setHoursPerWeek(2);
        contractToBeSaved.setTotalHours(52);  //52 total hours will be exactly 26 weeks

        assertDoesNotThrow(() -> contractService.validateContract(contractToBeSaved));
    }

    /**
     * OFF-POINT (also an IN-POINT) boundary test (total weeks > 26 throws exception).
     */
    @Test
    @Tag("ValidateContract")
    @Tag("BoundaryTest")
    void validateContractTotalWeeksExceeded() {
        contractToBeSaved.setHoursPerWeek(2);
        contractToBeSaved.setTotalHours(52.1);  //52 total hours will be exactly 26 weeks

        assertThrows(InvalidContractException.class,
                () -> contractService.validateContract(contractToBeSaved));
    }

    @Test
    @Tag("ValidateContract")
    void validateContractAlreadyExists() {
        String errorMsg = "Please cancel the existing contract (#" + contract.getId()
                + ") with this party.";

        when(contractRepository.findActiveContract(companyId, studentId)).thenReturn(contract);

        assertThrows(InvalidContractException.class,
                () -> contractService.validateContract(contractToBeSaved), errorMsg);
    }

    @Test
    @Tag("shouldExpire")
    void shouldNotExpireNullContract() {
        assertFalse(contractService.shouldExpire(null));
    }

    /**
     * ON-POINT (OUT POINT as well) -> (checks if status != ACTIVE)
     */
    @Test
    @Tag("shouldExpire")
    @Tag("BoundaryTest")
    void shouldExpireActiveContractSuccess() {
        contract.setEndDate(LocalDate.of(2021, 1, 1));
        contract.setStatus(ContractStatus.ACTIVE);
        assertTrue(contractService.shouldExpire(contract));
    }

    /**
     * OFF-POINT (IN POINT as well) -> (checks if status != ACTIVE)
     */
    @Test
    @Tag("shouldExpire")
    @Tag("BoundaryTest")
    void shouldNotExpireAlreadyExpiredContract() {
        contract.setStatus(ContractStatus.EXPIRED);
        assertFalse(contractService.shouldExpire(contract));
    }

    /**
     * OFF-POINT (IN POINT as well) -> (checks if status != ACTIVE)
     */
    @Test
    @Tag("shouldExpire")
    @Tag("BoundaryTest")
    void shouldNotExpireTerminatedContract() {
        contract.setStatus(ContractStatus.TERMINATED);
        assertFalse(contractService.shouldExpire(contract));
    }

    @Test
    @Tag("shouldExpire")
    void shouldExpireEndDateDidntPass() {
        contract.setEndDate(LocalDate.of(4096, 1, 1));
        assertFalse(contractService.shouldExpire(contract));
    }

    @Test
    @Tag("shouldExpire")
    void shouldExpireSuccess() {
        contract.setEndDate(LocalDate.of(2021, 1, 1));
        assertTrue(contractService.shouldExpire(contract));
    }

    @Test
    @Tag("checkAuthorization")
    void studentCanAccessContract() {
        assertDoesNotThrow(() -> contractService.checkAuthorization(contract, studentId));
    }

    @Test
    @Tag("checkAuthorization")
    void companyCanAccessContract() {
        assertDoesNotThrow(() -> contractService.checkAuthorization(contract, companyId));
    }

    @Test
    @Tag("checkAuthorization")
    void userCannotAccessContract() {
        assertThrows(AccessDeniedException.class,
                () -> contractService.checkAuthorization(contract, "BillGates"));
    }
}