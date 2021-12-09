package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.DTOs.requests.ContractRequest;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ContractController {

    @Autowired
    private transient ContractService contractService;

    /**
     * Validates a ContractRequest when creating a contract.
     * If a parameter in contractRequest is null,
     * a MethodArgumentNotValidException is thrown (from the @NotNull annotation),
     * which automatically triggers this method.
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
    public ResponseEntity<String> terminateContract(
            @RequestHeader("x-user-name") String userName,
            @PathVariable Long id) {

        try {
            contractService.terminateContract(id, userName);
            return ResponseEntity.ok().body(null);
        } catch (ContractNotFoundException | InactiveContractException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<Object> getContract(
            @RequestHeader("x-user-name") String userName,
            @PathVariable(name = "companyId") String companyId,
            @PathVariable(name = "studentId") String studentId) {
        try {
            Contract contract = contractService.getContract(companyId, studentId, true, userName);
            return ResponseEntity.ok().body(contract);
        } catch (ContractNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<Object> getMostRecentContract(
            @RequestHeader("x-user-name") String userName,
            @PathVariable(name = "companyId") String companyId,
            @PathVariable(name = "studentId") String studentId) {
        try {
            Contract contract = contractService.getContract(companyId, studentId, false, userName);
            return ResponseEntity.ok().body(contract);
        } catch (ContractNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}
