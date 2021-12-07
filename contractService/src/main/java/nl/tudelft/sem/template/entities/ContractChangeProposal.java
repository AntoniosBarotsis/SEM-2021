package nl.tudelft.sem.template.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.enums.changeStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContractChangeProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonProperty("contractId")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contractId", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Contract contract;

    private String proposer;
    private String participant;

    //Changes proposed (some could be null):
    private Double hoursPerWeek;
    private Double totalHours;
    private Double pricePerHour;
    //----------------------------------

    @Enumerated(EnumType.STRING)
    private changeStatus status;   //the status of the change proposal(pending/rejected/accepted)

    /**
     * Constructor for a contract change proposal, without the id.
     *
     * @param contract     The contract the change is proposed to.
     * @param proposer     The proposer of the change.
     * @param participant  The user (company or student) that is required to accept/decline the change.
     * @param hoursPerWeek Change in hours per week (may be null).
     * @param totalHours   Change in total hours (may be null).
     * @param pricePerHour Change in price per hour (may be null).
     * @param status       The status of the proposal (pending/rejected/accepted).
     */
    public ContractChangeProposal(Contract contract, String proposer, String participant,
                                  Double hoursPerWeek, Double totalHours,
                                  Double pricePerHour, changeStatus status) {
        this.contract = contract;
        this.proposer = proposer;
        this.participant = participant;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.status = status;
    }
}
