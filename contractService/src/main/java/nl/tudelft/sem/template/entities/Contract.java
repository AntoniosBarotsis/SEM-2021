package nl.tudelft.sem.template.entities;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.enums.Status;

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
    @Enumerated(EnumType.STRING)
    Status status;
}
