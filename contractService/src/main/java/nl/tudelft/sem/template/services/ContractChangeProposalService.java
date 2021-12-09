package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;
import nl.tudelft.sem.template.enums.ChangeStatus;
import nl.tudelft.sem.template.enums.ContractStatus;
import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ChangeProposalNotFoundException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidChangeProposalException;
import nl.tudelft.sem.template.repositories.ContractChangeProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractChangeProposalService {

    @Autowired
    private transient ContractChangeProposalRepository changeProposalRepository;

    @Autowired
    private transient ContractService contractService;

    private static final transient double MAX_HOURS = 20;
    private static final transient double MAX_WEEKS = 26;

    /**
     * Saves a change proposal to the repository.
     * Authorization is checked before this method is called.
     *
     * @param proposal The proposal that should be saved.
     * @return The saved proposal entity.
     * @throws InvalidChangeProposalException If the proposal is invalid
     * @throws InactiveContractException      If the contract is not active.
     */
    public ContractChangeProposal submitProposal(ContractChangeProposal proposal)
            throws InvalidChangeProposalException, InactiveContractException {
        // Check if proposal is valid:
        validateContractProposal(proposal);

        return changeProposalRepository.save(proposal);
    }

    /**
     * Accept a proposal and update the contract information.
     *
     * @param proposalId  The id of the proposal.
     * @param participant The user that accepts the proposal.
     * @return The updated contract.
     * @throws ChangeProposalNotFoundException If the proposal doesn't exist.
     * @throws InactiveContractException       If the contract is no longer active.
     * @throws InvalidChangeProposalException  If the proposal's parameters are no longer valid,
     *                                         meaning the contract was changed once before.
     *                                         (not likely to happen but just for caution)
     * @throws AccessDeniedException           If the participant is not in the contract.
     */
    public Contract acceptProposal(Long proposalId, String participant)
            throws ChangeProposalNotFoundException, InactiveContractException,
            InvalidChangeProposalException, AccessDeniedException {

        // Check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);

        // Check if the participant can reject the proposal and if contract is active:
        validateProposalAction(proposal, participant);

        changeProposalRepository.acceptProposal(proposalId);
        // Delete all past rejected proposals:
        changeProposalRepository.deleteAllRejectedProposalsOfContract(proposal.getContract());

        // Update contract and return the contract with applied changes:
        return contractService.updateContract(proposal.getContract(), proposal);
    }

    /**
     * Reject a proposal by its id.
     *
     * @param proposalId  The proposal's id.
     * @param participant The user that rejects the proposal.
     * @throws ChangeProposalNotFoundException If the proposal doesn't exist.
     * @throws InactiveContractException       If the contract is no longer active.
     * @throws AccessDeniedException           If the participant is not in the contract.
     */
    public void rejectProposal(Long proposalId, String participant)
            throws ChangeProposalNotFoundException, InactiveContractException,
            AccessDeniedException {

        // Check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);
        // Check if the participant can reject the proposal and if contract is active:
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
     * @throws AccessDeniedException           If the proposer is not in the contract.
     */
    public void deleteProposal(Long proposalId, String proposer)
            throws ChangeProposalNotFoundException, AccessDeniedException {
        // Check if proposal exists:
        ContractChangeProposal proposal = getProposal(proposalId);

        // Can't delete proposal if it was reviewed (accepted/rejected):
        if (proposal.getStatus() != ChangeStatus.PENDING) {
            throw new ChangeProposalNotFoundException(
                    "This proposal was already reviewed and cannot be deleted");
        }

        // Check if proposer is owner of the proposal:
        if (!proposal.getProposer().equals(proposer)) {
            throw new AccessDeniedException();
        }
        changeProposalRepository.deleteById(proposalId);
    }

    /**
     * Get all changes proposed for a contract.
     *
     * @param contract The contract the user checks for proposals.
     * @param userId   The id of the user.
     * @return List of all change proposals for that contract.
     * @throws ContractNotFoundException If the contract is not found.
     * @throws AccessDeniedException     If the user is not in the contract.
     * @throws InactiveContractException If the contract is no longer active.
     */
    public List<ContractChangeProposal> getProposals(Contract contract, String userId)
            throws ContractNotFoundException, AccessDeniedException, InactiveContractException {
        // Check if user is a participant in the contract:
        if (!contract.getCompanyId().equals(userId) && !contract.getStudentId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // Check if contract is still active:
        if (!contract.getStatus().equals(ContractStatus.ACTIVE)) {
            throw new InactiveContractException();
        }

        return changeProposalRepository.findAllByContract(contract);
    }

    //----------------------------------------
    //      PRIVATE METHODS:
    //----------------------------------------

    /**
     * PRIVATE HELPER METHOD which validates a contract change proposal's parameters.
     *
     * @param proposal The proposal to be validated.
     * @throws InvalidChangeProposalException Thrown when the proposal is not valid
     *                                        e.g. exceeds 20 hours per week, 6 month duration
     *                                        or the company's id and student's id are the same
     *                                        or if the contract is expired or cancelled
     *                                        or if the previous proposal wasn't reviewed.
     */
    private void validateContractProposal(ContractChangeProposal proposal)
            throws InvalidChangeProposalException, InactiveContractException {
        // When creating the proposal from a request there are
        // already checks to see if the 'proposer' is in the contract

        Contract contract = proposal.getContract();
        ContractStatus contractStatus = contract.getStatus();

        // Contract expired or terminated:
        if (contractStatus != ContractStatus.ACTIVE) {
            throw new InactiveContractException();
        }

        // If proposal didn't include hoursPerWeek or totalHours, use values from contract:
        double hoursPerWeek;
        double totalHours;
        if (proposal.getHoursPerWeek() != null) {
            hoursPerWeek = proposal.getHoursPerWeek();
        } else {
            hoursPerWeek = contract.getHoursPerWeek();
        }

        if (proposal.getTotalHours() != null) {
            totalHours = proposal.getTotalHours();
        } else {
            totalHours = contract.getTotalHours();
        }

        // Max no of hours exceeded OR no of weeks exceeded:
        if (hoursPerWeek > MAX_HOURS || totalHours / hoursPerWeek > MAX_WEEKS) {
            throw new InvalidChangeProposalException();
        }

        // If there already is a pending change proposal by this user:
        if (changeProposalRepository.findPendingChange(proposal.getContract(),
                proposal.getProposer()) != null) {
            throw new InvalidChangeProposalException(
                    "Your previous proposal hasn't been reviewed yet");
        }
    }

    /**
     * PRIVATE HELPER METHOD which checks if a change proposal can be accepted / rejected.
     *
     * @param proposal    The proposal to be validated.
     * @param participant The id of the user that wants to accept / reject the proposal.
     * @throws IllegalArgumentException Thrown when the participant isn't in the proposal
     *                                  (only the contract participant can accept/reject a proposal)
     *                                  or if the contract is expired or cancelled
     */
    private void validateProposalAction(ContractChangeProposal proposal, String participant)
            throws InactiveContractException, AccessDeniedException {

        // Participant is not in the contract:
        if (!proposal.getParticipant().equals(participant)) {
            throw new AccessDeniedException();
        }

        // Inactive contract:
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
}
