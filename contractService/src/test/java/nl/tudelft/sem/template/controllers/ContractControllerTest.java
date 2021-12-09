package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerTest {
    /*
    @Autowired
    private transient ContractController contractController;

    @MockBean
    private transient ContractService contractService;

    private transient Contract contract;
    private final transient String companyId = "TUDelft";
    private final transient String studentId = "JohnDoe";

    private transient ContractRequest contractRequest;
    private transient Contract contractFromRequest;

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.of(2021, 12, 25);
        LocalDate endDate = startDate.plusWeeks(3);
        contract = new Contract(1L, companyId, studentId, startDate, endDate, 14,
                42, 15, ContractStatus.ACTIVE);

        contractRequest = new ContractRequest(companyId, studentId, 14d, 42d, 15d);
        contractFromRequest = contractRequest.toContract();
    }

    @Test
    void createContractSuccess() {
        when(contractService.saveContract(contractFromRequest)).thenReturn(contract);

        assertEquals(new ResponseEntity<>(contract, HttpStatus.CREATED),
                contractController.createContract(contractRequest));
    }

    @Test
    void createContractFailed() {
        when(contractService.saveContract(contractFromRequest))
                .thenThrow(new IllegalArgumentException("error"));

        assertEquals(ResponseEntity.badRequest().body("error"),
                contractController.createContract(contractRequest));
    }

    @Test
    void terminateContractSuccess() {
        assertEquals(ResponseEntity.ok().body(null),
                contractController.terminateContract(contract.getId()));
    }

    @Test
    void terminateContractFailed() throws ContractNotFoundException, InactiveContractException {
        Exception e = new ContractNotFoundException(contract.getId());

        doThrow(e).when(contractService).terminateContract(any());

        assertEquals(ResponseEntity.badRequest().body(e.getMessage()),
                contractController.terminateContract(contract.getId()));
    }

    @Test
    void getContractSuccess() throws ContractNotFoundException {
        when(contractService.getContract(companyId, studentId, true)).thenReturn(contract);

        assertEquals(ResponseEntity.ok().body(contract),
                contractController.getContract(companyId, studentId));
    }

    @Test
    void getContractFailed() throws ContractNotFoundException {
        Exception e = new ContractNotFoundException(companyId, studentId);

        when(contractService.getContract(companyId, studentId, true)).thenThrow(e);

        assertEquals(ResponseEntity.notFound().build(),
                contractController.getContract(companyId, studentId));
    }

     */
}