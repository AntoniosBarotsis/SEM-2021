package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractController {

    @Autowired
    private transient ContractService contractService;
}
