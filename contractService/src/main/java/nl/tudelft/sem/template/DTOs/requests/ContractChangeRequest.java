package nl.tudelft.sem.template.DTOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractChangeRequest {
    Double hoursPerWeek;
    Double totalHours;
    Double pricePerHour;

    public boolean isValid(){
        return hoursPerWeek != null || totalHours != null || pricePerHour != null;
    }

    public ContractChangeProposal toContractChangeProposal(Contract contract){
        return null;
    }
}
