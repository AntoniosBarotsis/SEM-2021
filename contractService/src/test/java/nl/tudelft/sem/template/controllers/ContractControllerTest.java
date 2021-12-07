package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.services.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerTest {

    @Autowired
    private transient ContractController contractController;

    @MockBean
    private transient ContractService contractService;

    private transient Contract contract;
    private final transient String companyId = "TUDelft";
    private final transient String studentId = "JohnDoe";

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.of(2021, 12, 25);
        LocalDate endDate = startDate.plusWeeks(3);
        contract = new Contract(1L, companyId, studentId, startDate, endDate, 14,
                42, 15, Status.ACTIVE);
    }

    @Test
    void createContractSuccess() {
        when(contractService.saveContract(contract)).thenReturn(contract);

        assertEquals(new ResponseEntity<>(contract, HttpStatus.CREATED),
                contractController.createContract(contract));
    }

    @Test
    void createContractFailed() {
        when(contractService.saveContract(contract))
                .thenThrow(new IllegalArgumentException("error"));

        assertEquals(ResponseEntity.badRequest().body("error"),
                contractController.createContract(contract));
    }

    @Test
    void terminateContractSuccess() {
        assertEquals(ResponseEntity.ok().body(null),
                contractController.terminateContract(contract.getId()));
    }

    @Test
    void terminateContractFailed() throws ContractNotFoundException {
        Exception e = new ContractNotFoundException(contract.getId());

        doThrow(e).when(contractService).terminateContract(any());

        assertEquals(ResponseEntity.badRequest().body(e.getMessage()),
                contractController.terminateContract(contract.getId()));
    }

    @Test
    void getContractSuccess() throws ContractNotFoundException {
        when(contractService.getContract(companyId, studentId)).thenReturn(contract);

        assertEquals(ResponseEntity.ok().body(contract),
                contractController.getContract(companyId, studentId));
    }

    @Test
    void getContractFailed() throws ContractNotFoundException {
        Exception e = new ContractNotFoundException(companyId, studentId);

        when(contractService.getContract(companyId, studentId)).thenThrow(e);

        assertEquals(ResponseEntity.notFound().build(),
                contractController.getContract(companyId, studentId));
    }
}