package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;

}
