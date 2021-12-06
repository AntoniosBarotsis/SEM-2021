package nl.tudelft.sem.template.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public UserDoesNotExistException() {
    }

    public UserDoesNotExistException(String message) {
        super(message);
    }
}
