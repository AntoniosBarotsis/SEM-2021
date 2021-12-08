package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.DTOs.requests.ContractChangeRequest;
import nl.tudelft.sem.template.DTOs.requests.ContractRequest;
import nl.tudelft.sem.template.entities.Contract;
import nl.tudelft.sem.template.entities.ContractChangeProposal;
import nl.tudelft.sem.template.exceptions.ChangeProposalNotFoundException;
import nl.tudelft.sem.template.exceptions.ContractNotFoundException;
import nl.tudelft.sem.template.exceptions.InactiveContractException;
import nl.tudelft.sem.template.exceptions.InvalidChangeProposalException;
import nl.tudelft.sem.template.services.ContractChangeProposalService;
import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Path;
import java.util.List;

@RestController
public class ContractChangeProposalController {

    @Autowired
    private transient ContractChangeProposalService changeProposalService;

    @Autowired
    private transient ContractService contractService;

    /**
     * Submit a contract change proposal.
     *
     * @param contractId    The id of the contract the user wants to change.
     * @param proposer      The user that wants the change.
     * @param changeRequest The request containing the new contract parameters.
     * @return The saved proposal.
     */
    @PostMapping("/{contractId}/changeProposals/{proposer}")
    public ResponseEntity<Object> proposeChange(@PathVariable(name = "contractId") Long contractId,
                                                @PathVariable(name = "proposer") String proposer,
                                                @RequestBody ContractChangeRequest changeRequest) {
        try {
            Contract contract = contractService.getContract(contractId);
            ContractChangeProposal proposal =
                    changeRequest.toContractChangeProposal(contract, proposer);
            ContractChangeProposal p = changeProposalService.submitProposal(proposal);

            return new ResponseEntity<>(p, HttpStatus.CREATED);

        } catch (ContractNotFoundException | InvalidChangeProposalException
                | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeProposals/{proposalId}/accept/{participant}")
    public ResponseEntity<Object> acceptProposal(@PathVariable(name = "proposalId") Long proposalId,
                                                 @PathVariable(name = "participant") String participant) {

        try {
            Contract contract = changeProposalService.acceptProposal(proposalId, participant);
            return new ResponseEntity<>(contract, HttpStatus.OK);
        } catch (ChangeProposalNotFoundException | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeProposals/{proposalId}/reject/{participant}")
    public ResponseEntity<String> rejectProposal(@PathVariable(name = "proposalId") Long proposalId,
                                                 @PathVariable(name = "participant") String participant) {

        try {
            changeProposalService.rejectProposal(proposalId, participant);
            return ResponseEntity.ok(null);
        } catch (ChangeProposalNotFoundException | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeProposals/{proposalId}/delete/{proposer}")
    public ResponseEntity<String> deleteProposal(@PathVariable(name = "proposalId") Long proposalId,
                                                 @PathVariable(name = "proposer") String proposer) {

        try {
            changeProposalService.deleteProposal(proposalId, proposer);
            return ResponseEntity.ok(null);
        } catch (ChangeProposalNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{contractId}/changeProposals/{userId}")
    public ResponseEntity<Object> getProposalsOfContract(
            @PathVariable(name = "contractId") Long contractId,
            @PathVariable(name = "userId") String userId) {

        try {
            Contract contract = contractService.getContract(contractId);
            List<ContractChangeProposal> proposals =
                    changeProposalService.getProposals(contract, userId);
            return ResponseEntity.ok().body(proposals);
        } catch (ContractNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
