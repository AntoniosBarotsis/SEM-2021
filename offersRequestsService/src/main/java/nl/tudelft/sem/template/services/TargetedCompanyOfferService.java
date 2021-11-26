package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetedCompanyOfferService extends OfferService {

    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;

    public Offer saveOffer(TargetedCompanyOffer targetedCompanyOffer, Long id) throws IllegalArgumentException {
        StudentOffer studentOffer = studentOfferRepository.getById(id);
        if(studentOffer == null) {
            throw new IllegalArgumentException("Student offer does not exist");
        }
        targetedCompanyOffer.setStudentOffer(studentOffer);
        return super.saveOffer(targetedCompanyOffer);
    }
}
