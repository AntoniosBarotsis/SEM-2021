package nl.tudelft.sem.template.exceptions;

public class ContractNotFoundException extends Exception {
    public static final long serialVersionUID = 1234567;

    public ContractNotFoundException(String companyId, String studentId) {
        super("Could not find a contract between company " + companyId
                + " and student " + studentId);
    }
}
