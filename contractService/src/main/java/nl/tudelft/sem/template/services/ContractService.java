package nl.tudelft.sem.template.services;

import java.time.LocalDate;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
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
     * PRIVATE HELPER METHOD which finds the most recent active contract between 2 parties.
     * Can be used to check if an active contract already exists, or to get a contract.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return the found contract or null if not found.
     */
    private Optional<Contract> findActiveContract(String companyId, String studentId) {
        return contractRepository
                .findByCompanyIdEqualsAndStudentIdEqualsAndActiveTrue(companyId, studentId);
    }

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
        // contract between the same user:
        if (contract.getCompanyId().equals(contract.getStudentId())
                //an active contract already exists:
                || findActiveContract(contract.getCompanyId(), contract.getStudentId()).isPresent()

                || contract.getHoursPerWeek() > MAX_HOURS              //max no of hours exceeded

                //no of weeks exceeded:
                || contract.getTotalHours() / contract.getHoursPerWeek() > MAX_WEEKS) {
            throw new IllegalArgumentException("One or more contract parameters are invalid "
                    + "or one contract already exists");
        }
    }

    /**
     * Saves a contract in the repository.
     *
     * @param contract The contract to be saved.
     * @return The saved contract.
     * @throws IllegalArgumentException If the contract has invalid parameters
     *                                  or if one already exists.
     */
    public Contract saveContract(Contract contract) throws IllegalArgumentException {
        validateContract(contract);

        // Set startDate:
        if (contract.getStartDate() == null) {
            contract.setStartDate(LocalDate.now());
        }

        // Set endDate:
        if (contract.getEndDate() == null) {
            int weeks = (int) Math.ceil(contract.getTotalHours() / contract.getHoursPerWeek());
            LocalDate date = contract.getStartDate().plusWeeks(weeks);
            contract.setEndDate(date);    //LocalDate is immutable, so different memory address here
        }

        // Set as active
        contract.setActive(true);

        return contractRepository.save(contract);
    }

    /**
     * Gets the active contract between 2 parties from the repository.
     *
     * @param companyId The id of the company.
     * @param studentId The id of the student.
     * @return the found contract or null if not found.
     * @throws ContractNotFoundException Thrown if a contract doesn't exist.
     */
    public Contract getContract(String companyId, String studentId)
            throws ContractNotFoundException {
        Optional<Contract> contract = findActiveContract(companyId, studentId);

        if (contract.isEmpty()) {
            throw new ContractNotFoundException(companyId, studentId);
        } else {
            return contract.get();
        }
    }

    /**
     * Terminates a contract.
     *
     * @param contractId The contract that is to be terminated.
     * @throws ContractNotFoundException Thrown if a contract doesn't exist.
     */
    public void terminateContract(Long contractId) throws ContractNotFoundException {
        if (contractRepository.findById(contractId).isEmpty()) {
            throw new ContractNotFoundException(contractId);
        }
        contractRepository.terminateContract(contractId);
    }
}
