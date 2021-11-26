package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OfferController {

        @Autowired
        private OfferService offerService;
}
