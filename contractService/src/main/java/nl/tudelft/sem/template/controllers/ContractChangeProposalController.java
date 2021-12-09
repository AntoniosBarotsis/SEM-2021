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
     * @param changeRequest The request containing the new contract parameters.
     * @return The saved proposal.
     */
    @PostMapping("/{contractId}/changeProposals")
    public ResponseEntity<Object> proposeChange(
            @RequestHeader("x-user-name") String userName,
            @RequestHeader("x-user-role") String userRole,
            @PathVariable Long contractId,
            @RequestBody ContractChangeRequest changeRequest) {

        try {
            Contract contract = contractService.getContract(contractId);
            ContractChangeProposal proposal =
                    changeRequest.toContractChangeProposal(contract, userName);
            ContractChangeProposal p = changeProposalService.submitProposal(proposal);

            return new ResponseEntity<>(p, HttpStatus.CREATED);

        } catch (ContractNotFoundException | InvalidChangeProposalException
                | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeProposals/{proposalId}/accept")
    public ResponseEntity<Object> acceptProposal(
            @RequestHeader("x-user-name") String userName,
            @RequestHeader("x-user-role") String userRole,
            @PathVariable(name = "proposalId") Long proposalId) {

        try {
            Contract contract = changeProposalService.acceptProposal(proposalId, userName);
            return new ResponseEntity<>(contract, HttpStatus.OK);
        } catch (ChangeProposalNotFoundException | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeProposals/{proposalId}/reject")
    public ResponseEntity<String> rejectProposal(
            @RequestHeader("x-user-name") String userName,
            @RequestHeader("x-user-role") String userRole,
            @PathVariable(name = "proposalId") Long proposalId) {

        try {
            changeProposalService.rejectProposal(proposalId, userName);
            return ResponseEntity.ok(null);
        } catch (ChangeProposalNotFoundException | InactiveContractException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/changeProposals/{proposalId}")
    public ResponseEntity<String> deleteProposal(
            @RequestHeader("x-user-name") String userName,
            @RequestHeader("x-user-role") String userRole,
            @PathVariable(name = "proposalId") Long proposalId) {

        try {
            changeProposalService.deleteProposal(proposalId, userName);
            return ResponseEntity.ok(null);
        } catch (ChangeProposalNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{contractId}/changeProposals")
    public ResponseEntity<Object> getProposalsOfContract(
            @RequestHeader("x-user-name") String userName,
            @RequestHeader("x-user-role") String userRole,
            @PathVariable(name = "contractId") Long contractId) {

        try {
            Contract contract = contractService.getContract(contractId);
            List<ContractChangeProposal> proposals =
                    changeProposalService.getProposals(contract, userName);
            return ResponseEntity.ok().body(proposals);
        } catch (ContractNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
