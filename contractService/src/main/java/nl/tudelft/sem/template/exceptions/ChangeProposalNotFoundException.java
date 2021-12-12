package nl.tudelft.sem.template.exceptions;

public class ChangeProposalNotFoundException extends Exception {
    public static final long serialVersionUID = 1234569;

    public ChangeProposalNotFoundException(Long id) {
        super("Could not find a proposal with id = " + id);
    }

    public ChangeProposalNotFoundException(String msg) {
        super(msg);
    }
}