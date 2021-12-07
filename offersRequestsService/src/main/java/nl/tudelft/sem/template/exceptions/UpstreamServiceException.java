package nl.tudelft.sem.template.exceptions;

public class UpstreamServiceException extends Exception {
    public UpstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
