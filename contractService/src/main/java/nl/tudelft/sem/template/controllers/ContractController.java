package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.DTOs.requests.ContractRequest;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ContractController {

    @Autowired
    private transient ContractService contractService;

    /**
     * Validates a ContractRequest when creating a contract.
     * Automatically called when a MethodArgumentNotValidException
     * (from the @NotNull annotation) is thrown.
     * <p>
     * Method from: baeldung.com/spring-boot-bean-validation
     *
     * @param e The thrown exception containing the error message.
     * @return 400 BAD_REQUEST with all the fields that have been omitted.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException e) {

        // map <Field, Error> explaining which fields were null/empty
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Create a contract.
     *
     * @param contractRequest The contract to create.
     * @return 201 CREATED along with the created contract entity,
     * else 400 BAD REQUEST if there already exists a contract between the 2 parties.
     */
    @PostMapping("/")
    public ResponseEntity<Object> createContract(@Valid @RequestBody ContractRequest contractRequest) {
        try {
            Contract c = contractService.saveContract(contractRequest.toContract());
            return new ResponseEntity<>(c, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            //either a MethodArgumentNotValidException, or IllegalArgumentException
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Terminate an existing contract.
     *
     * @param id The id of the contract that should be terminated.
     * @return 200 OK if successful
     * else 400 BAD REQUEST if contract is not found, along with error message.
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
     * Get the current existing ACTIVE contract between 2 parties.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return 200 OK along with the contract between the company and student,
     * else 404 NOT FOUND if there is no active contract
     */
    @GetMapping("/{companyId}/{studentId}/current")
    public ResponseEntity<Contract> getContract(
            @PathVariable(name = "companyId") String companyId,
            @PathVariable(name = "studentId") String studentId) {
        try {
            Contract contract = contractService.getContract(companyId, studentId, true);
            return ResponseEntity.ok().body(contract);
        } catch (ContractNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get the most recent contract (active or not) between 2 parties.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return 200 OK along with the contract between the company and student,
     * else 404 NOT FOUND if there is no contract between the parties
     */
    @GetMapping("/{companyId}/{studentId}/mostRecent")
    public ResponseEntity<Contract> getMostRecentContract(
            @PathVariable(name = "companyId") String companyId,
            @PathVariable(name = "studentId") String studentId) {
        try {
            Contract contract = contractService.getContract(companyId, studentId, false);
            return ResponseEntity.ok().body(contract);
        } catch (ContractNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
