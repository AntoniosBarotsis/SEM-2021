package nl.tudelft.sem.template.repositories;

import java.util.List;
import javax.transaction.Transactional;
import lombok.NonNull;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentOfferRepository extends JpaRepository<StudentOffer, Long> {

    StudentOffer findByIdEquals(Long id);

    List<StudentOffer> findAllByExpertiseContainsAndStatusEquals(String expertise, Status status);

    List<StudentOffer> findAllByStudentIdEquals(String studentId);

    List<StudentOffer> findAllByStudentIdEqualsAndStatusEquals(String studentId, Status status);

    void deleteById(@NonNull Long id);

    //------------
    // UPDATES:
    //------------

    @Transactional
    @Modifying
    @Query("UPDATE StudentOffer o SET o.hoursPerWeek = ?2 WHERE o.id = ?1")
    void updateHoursPerWeek(Long studentOfferId, double hoursPerWeek);

    @Transactional
    @Modifying
    @Query("UPDATE StudentOffer o SET o.totalHours = ?2 WHERE o.id = ?1")
    void updateTotalHours(Long studentOfferId, double totalHours);

    @Transactional
    @Modifying
    @Query("UPDATE StudentOffer o SET o.expertise = ?2 WHERE o.id = ?1")
    void updateExpertise(Long studentOfferId, List<String> expertise);

    @Transactional
    @Modifying
    @Query("UPDATE StudentOffer o SET o.status = ?2 WHERE o.id = ?1")
    void updateStatus(Long studentOfferId, Status status);

    @Transactional
    @Modifying
    @Query("UPDATE StudentOffer o SET o.pricePerHour = ?2 WHERE o.id = ?1")
    void updatePricePerHour(Long studentOfferId, double pricePerHour);

}
