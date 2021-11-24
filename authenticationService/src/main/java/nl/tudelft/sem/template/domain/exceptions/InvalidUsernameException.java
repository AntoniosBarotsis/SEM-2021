package nl.tudelft.sem.template.domain.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
