package nl.tudelft.sem.template.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.enums.Status;


import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class NonTargetedCompanyOffer extends CompanyOffer {
    public NonTargetedCompanyOffer(String title, String description, double hoursPerWeek,
                                   double totalHours, List<String> expertise, Status status,
                                   List<String> requirements, String companyId) {
        super(title, description, hoursPerWeek, totalHours, expertise, status, requirements, companyId);
    }
}
