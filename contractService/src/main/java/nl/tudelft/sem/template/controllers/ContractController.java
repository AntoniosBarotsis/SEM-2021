package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractController {

    @Autowired
    private transient ContractService contractService;

    /**
     * Create a contract.
     *
     * @param contract The contract to create.
     * @return 201 CREATED along with the created contract entity,
     *         else 400 BAD REQUEST if there already exists a contract between the 2 parties.
     */
    @PostMapping("/")
    public ResponseEntity<Object> createContract(@RequestBody Contract contract) {
        try {
            Contract c = contractService.saveContract(contract);
            return new ResponseEntity<>(c, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Terminate an existing contract.
     *
     * @param id The id of the contract that should be terminated.
     * @return 200 OK if successful
     *         else 400 BAD REQUEST if contract is not found, along with error message.
     */
    @PutMapping("/{id}/terminate")
    public ResponseEntity<String> terminateContract(@PathVariable Long id) {
        try {
            contractService.terminateContract(id);
            return ResponseEntity.ok().body(null);
        } catch (ContractNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get an existing ACTIVE contract between 2 parties.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return 200 OK along with the contract between the company and student,
     *         else 404 NOT FOUND if there is no active contract
     */
    @GetMapping("/{companyId}/{studentId}")
    public ResponseEntity<Contract> getContract(
            @PathVariable(name = "companyId") String companyId,
            @PathVariable(name = "studentId") String studentId) {
        try {
            Contract contract = contractService.getContract(companyId, studentId);
            return ResponseEntity.ok().body(contract);
        } catch (ContractNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
