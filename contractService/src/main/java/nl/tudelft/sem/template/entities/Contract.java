package nl.tudelft.sem.template.entities;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
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
    double totalHours;
    double pricePerHour;
    boolean active;

    /**
     * Constructor for the Contract class WITHOUT THE ID.
     *
     * @param companyId    String with company's id.
     * @param studentId    String with student's id.
     * @param startDate    LocalDate indicating when the contract started.
     * @param endDate      LocalDate indicating when the contract ends.
     * @param hoursPerWeek Double indicating how many hours per week.
     * @param totalHours   Double indicating the total amount of hours.
     * @param pricePerHour Double indicating the price per hour.
     * @param active       Boolean indicating whether the contract is active or not.
     */
    public Contract(String companyId, String studentId, LocalDate startDate, LocalDate endDate,
                    double hoursPerWeek, double totalHours, double pricePerHour, boolean active) {
        this.companyId = companyId;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.active = active;
    }

    /**
     * Constructor for the Contract class without the ID and boolean active.
     *
     * @param companyId    String with company's id.
     * @param studentId    String with student's id.
     * @param startDate    LocalDate indicating when the contract started.
     * @param endDate      LocalDate indicating when the contract ends.
     * @param hoursPerWeek Double indicating how many hours per week.
     * @param totalHours   Double indicating the total amount of hours.
     * @param pricePerHour Double indicating the price per hour.
     */
    public Contract(String companyId, String studentId, LocalDate startDate, LocalDate endDate,
                    double hoursPerWeek, double totalHours, double pricePerHour) {
        this.companyId = companyId;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.active = true;
    }
}
