package nl.tudelft.sem.template.repositories;

import java.util.Optional;
import nl.tudelft.sem.template.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByCompanyIdEqualsAndStudentIdEqualsAndActiveTrue(
            String companyId, String studentId);

    @Transactional
    @Modifying
    @Query("UPDATE Contract c SET c.active = false WHERE c.id = ?1")
    void terminateContract(Long contractId);

}
