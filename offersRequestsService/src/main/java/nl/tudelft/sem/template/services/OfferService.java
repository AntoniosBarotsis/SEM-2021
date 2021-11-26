package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.repositories.NonTargetedCompanyOfferRepository;
import nl.tudelft.sem.template.repositories.OfferRepository;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

        @Autowired
        private OfferRepository offerRepository;

        @Autowired
        private StudentOfferRepository studentOfferRepository;

        @Autowired
        private TargetedCompanyOfferRepository targetedCompanyOfferRepository;

        @Autowired
        private NonTargetedCompanyOfferRepository nonTargetedCompanyOfferRepository;
}
