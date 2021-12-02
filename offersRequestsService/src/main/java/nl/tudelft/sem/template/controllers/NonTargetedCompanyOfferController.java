package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.NonTargetedCompanyOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NonTargetedCompanyOfferController {

    @Autowired
    private transient NonTargetedCompanyOfferService nonTargetedCompanyOfferService;
}
