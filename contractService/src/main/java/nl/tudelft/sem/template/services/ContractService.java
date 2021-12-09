package nl.tudelft.sem.template.services;

import java.time.LocalDate;
import java.util.Optional;

import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;
import nl.tudelft.sem.template.enums.ContractStatus;
import nl.tudelft.sem.template.exceptions.AccessDeniedException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidChangeProposalException;
import nl.tudelft.sem.template.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractService {

    @Autowired
    private transient ContractRepository contractRepository;

    private static final transient double MAX_HOURS = 20;
    private static final transient double MAX_WEEKS = 26;

    /**
     * Saves a contract in the repository.
     *
     * @param contract The contract to be saved.
     * @return The saved contract.
     * @throws IllegalArgumentException If the contract has invalid parameters
     *                                  or if one already exists.
     */
    public Contract saveContract(Contract contract) throws IllegalArgumentException {
        // Check contract parameters:
        validateContract(contract);

        // Set startDate:
        contract.setStartDate(LocalDate.now());

        // Set endDate:
        int weeks = (int) Math.ceil(contract.getTotalHours() / contract.getHoursPerWeek());
        LocalDate date = contract.getStartDate().plusWeeks(weeks);
        contract.setEndDate(date);    //LocalDate is immutable, so different memory address here

        // Set as active
        contract.setStatus(ContractStatus.ACTIVE);

        return contractRepository.save(contract);
    }

    /**
     * Gets the contract (active or not) between 2 parties from the repository.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @param active    If the query needs an active contract or not.
     * @param userId    The id of the user that wants to get the contract.
     * @return the found contract or null if not found.
     * @throws ContractNotFoundException Thrown if a contract doesn't exist.
     */
    public Contract getContract(String companyId, String studentId, boolean active, String userId)
            throws ContractNotFoundException, AccessDeniedException {
        // Check authorization:
        if (!companyId.equals(userId) && !studentId.equals(userId))
            throw new AccessDeniedException();

        Contract contract;
        // If we need the current active contract:
        if (active) {
            // Return current active contract:
            contract = contractRepository.findActiveContract(companyId, studentId);
        } else {
            // Return most recent (active or not) contract:
            contract = contractRepository
                    .findFirstByCompanyIdEqualsAndStudentIdEqualsOrderByStartDateDesc(
                            companyId, studentId
                    );
        }

        if (contract == null) {
            throw new ContractNotFoundException(companyId, studentId);
        } else {
            return contract;
        }
    }

    /**
     * Get a contract by the passed id.
     * Also used to check if a contract exists.
     * No authorization checks here because it's not used by the controllers.
     *
     * @param contractId The id of the contract.
     * @throws ContractNotFoundException If the contract doesn't exist.
     */
    public Contract getContract(Long contractId) throws ContractNotFoundException {
        Optional<Contract> contract = contractRepository.findById(contractId);

        // If contract doesn't exist throw exception:
        if (contract.isEmpty()) {
            throw new ContractNotFoundException(contractId);
        }
        return contract.get();
    }

    /**
     * Terminates a contract.
     *
     * @param contractId The contract that is to be terminated.
     * @param userId     The id of the user that wants the contract terminated.
     * @throws ContractNotFoundException Thrown if a contract doesn't exist.
     */
    public void terminateContract(Long contractId, String userId)
            throws ContractNotFoundException, InactiveContractException, AccessDeniedException {
        // Check if contract exists:
        Contract contract = getContract(contractId);

        // Check if user is a participant in the contract:
        checkAuthorization(contract, userId);

        // Terminate contract only if it is active:
        if (!contract.getStatus().equals(ContractStatus.ACTIVE))
            throw new InactiveContractException();

        contractRepository.terminateContract(contractId);
    }

    /**
     * Updates a contract when a proposed change is accepted.
     * <br><br>
     * This method is called only if the contract is active, so no checks for that are needed.
     * No authorization checks because they are done before the call to this method.
     * <br>
     *
     * @param contract The contract that should be changed.
     * @param proposal The proposed changes.
     * @return The updated contract entity.
     */
    public Contract updateContract(Contract contract, ContractChangeProposal proposal)
            throws InvalidChangeProposalException {
        // Update contract with values from proposal:
        if (proposal.getHoursPerWeek() != null)
            contract.setHoursPerWeek(proposal.getHoursPerWeek());

        if (proposal.getTotalHours() != null) contract.setTotalHours(proposal.getTotalHours());

        if (proposal.getPricePerHour() != null)
            contract.setPricePerHour(proposal.getPricePerHour());


        // Check if too many hours per week or too many weeks:
        if (contract.getHoursPerWeek() > MAX_HOURS
                || contract.getTotalHours() / contract.getHoursPerWeek() > MAX_WEEKS)
            throw new InvalidChangeProposalException("This change proposal is not valid anymore, "
                    + "due to past modifications to the contract.");

        // Set new endDate:
        int weeks = (int) Math.ceil(contract.getTotalHours() / contract.getHoursPerWeek());
        LocalDate date = contract.getStartDate().plusWeeks(weeks);
        contract.setEndDate(date);

        return contractRepository.save(contract);
    }

    //----------------------------------------
    //      PRIVATE METHODS:
    //----------------------------------------

    /**
     * PRIVATE HELPER METHOD which validates a contract's parameters.
     *
     * @param contract The contract to be validated.
     * @throws IllegalArgumentException Thrown when the contract is not valid
     *                                  e.g. exceeds 20 hours per week, 6 month duration
     *                                  or the company's id and student's id are the same
     *                                  or if there is an existing active contract already
     */
    private void validateContract(Contract contract) throws IllegalArgumentException {
        // Contract between the same user:
        if (contract.getCompanyId().equals(contract.getStudentId())

                // Max no of hours exceeded:
                || contract.getHoursPerWeek() > MAX_HOURS

                // No of weeks exceeded:
                || contract.getTotalHours() / contract.getHoursPerWeek() > MAX_WEEKS) {

            throw new IllegalArgumentException("One or more contract parameters are invalid.");
        }
        if (contractRepository.findActiveContract(contract.getCompanyId(),
                contract.getStudentId()) != null) {
            throw new IllegalArgumentException(
                    "Please cancel the existing contract with this party.");
        }
    }

    private void checkAuthorization(Contract contract, String userId)
            throws AccessDeniedException {
        if (!contract.getStudentId().equals(userId) && !contract.getCompanyId().equals(userId))
            throw new AccessDeniedException();
    }

}
