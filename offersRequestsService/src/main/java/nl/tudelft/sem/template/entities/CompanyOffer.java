package nl.tudelft.sem.template.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.converters.StringListConverter;
import nl.tudelft.sem.template.enums.Status;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public abstract class CompanyOffer extends Offer {
    @Convert(converter = StringListConverter.class)
    private List<String> requirements;
    private String companyId;

    public CompanyOffer(String title, String description, double hoursPerWeek,
                        double totalHours, List<String> expertise, Status status,
                        List<String> requirements, String companyId) {
        super(title, description, hoursPerWeek, totalHours, expertise, status);
        this.requirements = requirements;
        this.companyId = companyId;
    }
}
