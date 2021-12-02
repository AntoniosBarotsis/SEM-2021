package nl.tudelft.sem.template.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String companyId;
    String studentId;
    LocalDate startDate;
    LocalDate endDate;
    double hoursPerWeek;
    double pricePerHour;
    boolean active;

    /** Constructor for the Contract class.
     *
     * @param companyId        String with company's id.
     * @param studentId        String with student's id.
     * @param startDate        LocalDate indicating when the contract started.
     * @param endDate          LocalDate indicating when the contract ends.
     * @param hoursPerWeek     Double indicating how many hours per week.
     * @param pricePerHour     Double indicating the price per hour.
     * @param active           Boolean indicating whether the contract is active or not.
     */
    public Contract(String companyId, String studentId, LocalDate startDate,
                    LocalDate endDate, double hoursPerWeek, double pricePerHour, boolean active) {
        this.companyId = companyId;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursPerWeek = hoursPerWeek;
        this.pricePerHour = pricePerHour;
        this.active = active;
    }
}
