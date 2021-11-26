package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetedCompanyOfferService extends OfferService {

    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

}
