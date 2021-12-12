package nl.tudelft.sem.template.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.enums.ContractStatus;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String companyId;
    private String studentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double hoursPerWeek;
    private double totalHours;
    private double pricePerHour;
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "contract")
    private List<ContractChangeProposal> proposedChanges;

    /**
     * Constructor for the Contract class WITHOUT THE ID.
     *
     * @param id             The contract's id.
     * @param companyId      String with company's id.
     * @param studentId      String with student's id.
     * @param startDate      LocalDate indicating when the contract started.
     * @param endDate        LocalDate indicating when the contract ends.
     * @param hoursPerWeek   Double indicating how many hours per week.
     * @param totalHours     Double indicating the total amount of hours.
     * @param pricePerHour   Double indicating the price per hour.
     * @param status Enum indicating whether the contract is active, expired or terminated.
     */
    public Contract(Long id, String companyId, String studentId, LocalDate startDate,
                    LocalDate endDate, double hoursPerWeek, double totalHours,
                    double pricePerHour, ContractStatus status) {
        this.id = id;
        this.companyId = companyId;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.status = status;
    }
}
