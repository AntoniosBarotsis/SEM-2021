package nl.tudelft.sem.template.repositories;

import java.util.List;
import javax.transaction.Transactional;
import lombok.NonNull;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TargetedCompanyOfferRepository extends JpaRepository<TargetedCompanyOffer, Long> {


    //------------
    // UPDATES:
    //------------

    @Transactional
    @Modifying
    @Query("UPDATE TargetedCompanyOffer o SET o.hoursPerWeek = ?2 WHERE o.id = ?1")
    void updateHoursPerWeek(Long studentOfferId, double hoursPerWeek);

    @Transactional
    @Modifying
    @Query("UPDATE TargetedCompanyOffer o SET o.totalHours = ?2 WHERE o.id = ?1")
    void updateTotalHours(Long studentOfferId, double totalHours);

    @Transactional
    @Modifying
    @Query("UPDATE TargetedCompanyOffer o SET o.expertise = ?2 WHERE o.id = ?1")
    void updateExpertise(Long studentOfferId, List<String> expertise);

    @Transactional
    @Modifying
    @Query("UPDATE TargetedCompanyOffer o SET o.status = ?2 WHERE o.id = ?1")
    void updateStatus(Long studentOfferId, Status status);

    @Transactional
    @Modifying
    @Query("UPDATE TargetedCompanyOffer o SET o.requirements = ?2 WHERE o.id = ?1")
    void updateRequirements(Long studentOfferId, List<String> requirements);

}
