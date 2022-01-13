package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidContractException;
import nl.tudelft.sem.template.interfaces.ContractControllerHelperInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ContractControllerHelper implements ContractControllerHelperInterface {

    /**
     * Returns a response entity with an error message and a different status
     * for different exceptions.
     *
     * @param e The exception that was thrown.
     * @return A response entity with different statuses for different exceptions.
     */
    public ResponseEntity<Object> getResponseEntityForException(Exception e) {
        if (e instanceof ContractNotFoundException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        if (e instanceof AccessDeniedException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        if (e instanceof InvalidContractException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (e instanceof InactiveContractException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
