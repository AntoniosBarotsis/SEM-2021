package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

}
