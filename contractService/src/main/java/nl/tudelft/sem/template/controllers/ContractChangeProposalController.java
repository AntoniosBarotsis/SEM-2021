package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.ContractChangeProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractChangeProposalController {

    @Autowired
    private transient ContractChangeProposalService changeProposalService;
}
