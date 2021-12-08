package nl.tudelft.sem.template.DTOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;
import nl.tudelft.sem.template.enums.ChangeStatus;
import nl.tudelft.sem.template.exceptions.InvalidChangeProposalException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractChangeRequest {
    Double hoursPerWeek;
    Double totalHours;
    Double pricePerHour;

    /**
     * Checks if a contractChangeRequest is valid.
     * True if at least one parameter is not null.
     *
     * @return true if it's valid, false if not.
     */
    public boolean isValid() {
        return hoursPerWeek != null || totalHours != null || pricePerHour != null;
    }

    /**
     * Convert a contractChangeRequest to a contractChangeProposal ENTITY.
     *
     * @param contract The contract the request is submitted to.
     * @param proposer The user that proposed the change to the contract.
     * @return The contractChangeProposal entity.
     * @throws InvalidChangeProposalException If the proposer is not in the contract.
     */
    public ContractChangeProposal toContractChangeProposal(Contract contract, String proposer)
            throws InvalidChangeProposalException {
        if (!isValid()) throw new InvalidChangeProposalException();

        String participant;
        if (proposer.equals(contract.getCompanyId())) {
            participant = contract.getStudentId();
        } else if (proposer.equals(contract.getStudentId())) {
            participant = contract.getCompanyId();
        } else {
            throw new InvalidChangeProposalException();
        }

        return new ContractChangeProposal(contract, proposer, participant, hoursPerWeek,
                totalHours, pricePerHour, ChangeStatus.PENDING);
    }
}
