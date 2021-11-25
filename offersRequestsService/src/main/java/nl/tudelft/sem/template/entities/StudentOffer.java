package nl.tudelft.sem.template.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.enums.Status;

import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class StudentOffer extends Offer {
    private double pricePerHour;
    private String studentId;

    public StudentOffer(String title, String description, double hoursPerWeek,
                        double totalHours, List<String> expertise, Status status,
                        double pricePerHour, String studentId) {
        super(title, description, hoursPerWeek, totalHours, expertise, status);
        this.pricePerHour = pricePerHour;
        this.studentId = studentId;
    }
}
