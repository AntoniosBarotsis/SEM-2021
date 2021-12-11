package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import logger.FileLogger;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.repositories.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private final transient String companyId = "TUDelft";
    private final transient String studentId = "JohnDoe";
    private final transient String invalidParamsError =
            "One or more contract parameters are invalid.";
    private final transient String contractExistsError =
            "Please cancel the existing contract with this party.";

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.of(2021, 12, 25);
        LocalDate endDate = startDate.plusWeeks(3);
        contract = new Contract(1L, companyId, studentId, startDate, endDate, 14,
                42, 15, Status.ACTIVE);
    }

    @Test
    void saveValidContractSuccess() {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);
        contract.setStatus(null);     //not active
        contract.setEndDate(null);     //no end date!
        when(contractRepository.save(contract))
                .thenReturn(contract);
        contractService.saveContract(contract);

        contract.setEndDate(LocalDate.of(2021, 12, 25).plusWeeks(3));
        contract.setStatus(Status.ACTIVE);

        ArgumentCaptor<Contract> contractArgumentCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository).save(contractArgumentCaptor.capture());
        assertEquals(contract, contractArgumentCaptor.getValue());
    }

    @Test
    void saveInvalidContractSameParties() {
        contract.setStudentId(contract.getCompanyId());
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> contractService.saveContract(contract),
                invalidParamsError);
    }

    @Test
    void saveInvalidContractExceedsHoursPerWeek() {
        contract.setHoursPerWeek(20.1);
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> contractService.saveContract(contract),
                invalidParamsError);
    }

    @Test
    void saveInvalidContractExceedsMaxWeeks() {
        contract.setHoursPerWeek(2);
        contract.setTotalHours(52.5);  //52 total hours is exactly 26 weeks
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> contractService.saveContract(contract));
    }

    @Test
    void saveContractAlreadyExists() {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(contract);

        assertThrows(IllegalArgumentException.class, () -> contractService.saveContract(contract),
                contractExistsError);
    }

    @Test
    void getContractSuccess() throws ContractNotFoundException {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(contract);

        assertEquals(contractService.getContract(companyId, studentId), contract);
    }

    @Test
    void getContractNotFoundError() {
        when(contractRepository
                .findActiveContract(companyId, studentId))
                .thenReturn(null);

        assertThrows(ContractNotFoundException.class,
                () -> contractService.getContract(companyId, studentId));
    }

    @Test
    void terminateContractSuccess() throws ContractNotFoundException {
        when(contractRepository.findById(contract.getId()))
                .thenReturn(Optional.of(contract));

        contractService.terminateContract(contract.getId());

        verify(contractRepository).terminateContract(contract.getId());
    }

    @Test
    void terminateContractNotFound() {
        when(contractRepository.findById(contract.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class,
                () -> contractService.terminateContract(contract.getId()));
    }

}