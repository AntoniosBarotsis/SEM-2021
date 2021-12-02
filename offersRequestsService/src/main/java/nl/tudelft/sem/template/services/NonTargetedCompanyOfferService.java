package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.repositories.ApplicationRepository;
import nl.tudelft.sem.template.repositories.NonTargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NonTargetedCompanyOfferService extends OfferService {

    @Autowired
    private transient NonTargetedCompanyOfferRepository nonTargetedCompanyOfferRepository;

    @Autowired
    private transient ApplicationRepository applicationRepository;

}
