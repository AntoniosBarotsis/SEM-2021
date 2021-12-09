package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;
import nl.tudelft.sem.template.enums.ChangeStatus;
import nl.tudelft.sem.template.enums.ContractStatus;
import nl.tudelft.sem.template.exceptions.ChangeProposalNotFoundException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidChangeProposalException;
import nl.tudelft.sem.template.repositories.ContractChangeProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractChangeProposalService {

    @Autowired
    private transient ContractChangeProposalRepository changeProposalRepository;

    @Autowired
    private transient ContractService contractService;

    private static final transient double MAX_HOURS = 20;
    private static final transient double MAX_WEEKS = 26;

    /**
     * PRIVATE HELPER METHOD which validates a contract change proposal's parameters.
     *
     * @param proposal The proposal to be validated.
     * @throws InvalidChangeProposalException Thrown when the proposal is not valid
     *                                        e.g. exceeds 20 hours per week, 6 month duration
     *                                        or the company's id and student's id are the same
     *                                        or if the contract is expired or cancelled
     */
    private void validateContractProposal(ContractChangeProposal proposal)
            throws InvalidChangeProposalException, InactiveContractException {
        // when creating the proposal from a request there are
        // already checks to see if the 'proposer' is in the contract

        Contract contract = proposal.getContract();
        ContractStatus contractStatus = proposal.getContract().getStatus();

        //contract expired or terminated:
        if (contractStatus != ContractStatus.ACTIVE) {
            throw new InactiveContractException();
        }

        //if proposal didn't include hoursPerWeek or totalHours,
        //use values from contract:
        double hoursPerWeek = contract.getHoursPerWeek();
        double totalHours = contract.getTotalHours();
        if (proposal.getHoursPerWeek() != null) hoursPerWeek = proposal.getHoursPerWeek();
        if (proposal.getTotalHours() != null) totalHours = proposal.getTotalHours();

        //max no of hours exceeded:
        if (hoursPerWeek > MAX_HOURS

                //no of weeks exceeded:
                || totalHours / hoursPerWeek > MAX_WEEKS) {

            throw new InvalidChangeProposalException();
        }

        //if there already is a pending change proposal by this user:
        if (changeProposalRepository.findPendingChange(proposal.getContract(),
                proposal.getProposer()) != null) {
            throw new InvalidChangeProposalException(
                    "Your previous proposal hasn't been reviewed yet");
        }
    }

    /**
     * PRIVATE HELPER METHOD which checks if a change proposal can be accepted / rejected.
     *
     * @param proposal The proposal to be validated.
     * @throws IllegalArgumentException Thrown when the participant isn't in the proposal
     *                                  (only the contract participant can accept/reject a proposal)
     *                                  or if the contract is expired or cancelled
     */
    private void validateProposalAction(ContractChangeProposal proposal, String participant)
            throws ChangeProposalNotFoundException, InactiveContractException {

        //participant is not in the contract:
        if (!proposal.getParticipant().equals(participant)) {
            throw new ChangeProposalNotFoundException(proposal.getId());
        }

        //inactive contract:
        if (!proposal.getContract().getStatus().equals(ContractStatus.ACTIVE)) {
            throw new InactiveContractException();
        }
    }

    /**
     * PRIVATE method to get a proposal by the passed id.
     * Also used to check if a proposal exists.
     *
     * @param proposalId The id of the proposal.
     * @throws ChangeProposalNotFoundException If the proposal doesn't exist.
     */
    private ContractChangeProposal getProposal(Long proposalId)
            throws ChangeProposalNotFoundException {

        Optional<ContractChangeProposal> p = changeProposalRepository.findById(proposalId);

        if (p.isEmpty()) {
            throw new ChangeProposalNotFoundException(proposalId);
        } else {
            return p.get();
        }
    }

    /**
     * Saves a change proposal to the repository.
     *
     * @param proposal The proposal that should be saved.
     * @return The saved proposal entity.
     * @throws InvalidChangeProposalException If the proposal is invalid
     *                                        or the contract is not active.
     */
    public ContractChangeProposal submitProposal(ContractChangeProposal proposal)
            throws InvalidChangeProposalException, InactiveContractException {
        //check if proposal is valid:
        validateContractProposal(proposal);

        return changeProposalRepository.save(proposal);
    }

    /**
     * Accept a proposal and update the contract information.
     *
     * @param proposalId  The id of the proposal.
     * @param participant The user that accepts the proposal.
     * @return The updated contract.
     * @throws ChangeProposalNotFoundException If the participant is not in the contract
     *                                         or if the proposal doesn't exist.
     */
    public Contract acceptProposal(Long proposalId, String participant)
            throws ChangeProposalNotFoundException, InactiveContractException {

        //check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);
        //check if the participant can reject the proposal and if contract is active:
        validateProposalAction(proposal, participant);

        changeProposalRepository.acceptProposal(proposalId);
        changeProposalRepository.deleteAllRejectedProposalsOfContract(proposal.getContract());

        return contractService.updateContract(proposal.getContract(), proposal);
    }

    /**
     * Reject a proposal by its id.
     *
     * @param proposalId  The proposal's id.
     * @param participant The user that rejects the proposal.
     * @throws ChangeProposalNotFoundException If the participant is not in the contract
     *                                         or if the proposal doesn't exist.
     */
    public void rejectProposal(Long proposalId, String participant)
            throws ChangeProposalNotFoundException, InactiveContractException {

        //check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);
        //check if the participant can reject the proposal and if contract is active:
        validateProposalAction(proposal, participant);

        changeProposalRepository.rejectProposal(proposalId);
    }

    /**
     * Delete a proposal by its id.
     *
     * @param proposalId The id of the proposal.
     * @param proposer   The user that proposed the change and wants to delete it.
     * @throws ChangeProposalNotFoundException If the proposer is not in the contract
     *                                         or if the proposal doesn't exist.
     */
    public void deleteProposal(Long proposalId, String proposer)
            throws ChangeProposalNotFoundException {
        //check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);

        if (proposal.getStatus() != ChangeStatus.PENDING)
            throw new ChangeProposalNotFoundException(
                    "This proposal was already reviewed and cannot be deleted");

        //check if proposer is owner of the proposal:
        if (!proposal.getProposer().equals(proposer))
            throw new ChangeProposalNotFoundException(proposalId);

        changeProposalRepository.deleteById(proposalId);
    }

    public List<ContractChangeProposal> getProposals(Contract contract, String userId)
            throws ContractNotFoundException {
        if (!contract.getCompanyId().equals(userId) && !contract.getStudentId().equals(userId)) {
            throw new ContractNotFoundException(contract.getId());
        }

        return changeProposalRepository.findAllByContract(contract);
    }
}
