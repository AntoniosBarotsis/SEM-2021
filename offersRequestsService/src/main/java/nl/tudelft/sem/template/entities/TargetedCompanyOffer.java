package nl.tudelft.sem.template.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.enums.Status;

import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class TargetedCompanyOffer  extends CompanyOffer{
    private String studentId;

    public TargetedCompanyOffer(String title, String description, double hoursPerWeek,
                                double totalHours, List<String> expertise, Status status,
                                List<String> requirements, String companyId, String studentId) {
        super(title, description, hoursPerWeek, totalHours, expertise, status, requirements, companyId);
        this.studentId = studentId;
    }
}
