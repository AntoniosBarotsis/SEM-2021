package nl.tudelft.sem.template.controllers;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import nl.tudelft.sem.template.dtos.requests.ContractRequest;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidContractException;
import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractController {

    @Autowired
    private transient ContractService contractService;

    private final transient String nameHeader = "x-user-name";

    /**
     * Validates a ContractRequest when creating a contract.
     * If a parameter in contractRequest is null,
     * a MethodArgumentNotValidException is thrown (from the @NotNull annotation),
     * which automatically triggers this method.
     *
     * <p>Method from: baeldung.com/spring-boot-bean-validation
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

    // NEED TO MAKE THIS AS A PRIVATE ENDPOINT (NOT PUBLICLY AVAILABLE)
    /**
     * Create a contract.
     *
     * @param contractRequest The contract to create.
     * @return 201 CREATED along with the created contract entity,
     *         else 400 BAD REQUEST if there already exists a contract between the 2 parties
     *         or if the contract has invalid parameters.
     */
    @PostMapping("/")
    public ResponseEntity<Object> createContract(
            @Valid @RequestBody ContractRequest contractRequest) {
        try {
            Contract c = contractService.saveContract(contractRequest.toContract());
            return new ResponseEntity<>(c, HttpStatus.CREATED);
        } catch (InvalidContractException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Terminate an existing contract.
     *
     * @param userName The id of the user making the request.
     * @param id       The id of the contract that should be terminated.
     * @return 200 OK if successful,
     *         400 BAD REQUEST if the contract is not active,
     *         404 NOT FOUND if the contract is not found,
     *         401 UNAUTHORIZED if the contract doesn't belong to the user.
     */
    @PutMapping("/{id}/terminate")
    public ResponseEntity<String> terminateContract(
            @RequestHeader(nameHeader) String userName,
            @PathVariable Long id) {

        try {
            contractService.terminateContract(id, userName);
            return ResponseEntity.ok().body(null);
        } catch (ContractNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InactiveContractException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Get the current existing ACTIVE contract between 2 parties.
     *
     * @param userName  The id of the user making the request.
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return 200 OK along with the contract between the company and student,
     *         404 NOT FOUND if there is no current active contract,
     *         401 UNAUTHORIZED if the contract doesn't belong to the user.
     */
    @GetMapping("/{companyId}/{studentId}/current")
    public ResponseEntity<Object> getContract(
            @RequestHeader(nameHeader) String userName,
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
     * @param userName The id of the user making the request.
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return 200 OK along with the contract between the company and student,
     *         404 NOT FOUND if there is no contract between the parties,
     *         401 UNAUTHORIZED if the contract doesn't belong to the user.
     */
    @GetMapping("/{companyId}/{studentId}/mostRecent")
    public ResponseEntity<Object> getMostRecentContract(
            @RequestHeader(nameHeader) String userName,
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
