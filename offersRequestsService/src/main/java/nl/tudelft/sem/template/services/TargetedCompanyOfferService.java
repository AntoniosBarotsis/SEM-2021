package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * Service, which profides Targeted offers, created by a specific Company.
     *
     * @param companyId - the ID of the Company.
     * @return - A list of Targeted Requests, which are all created by the Company.
     */
    public List<TargetedCompanyOffer> getOffersById(String companyId) {
        List<TargetedCompanyOffer> offers = targetedCompanyOfferRepository.findAllByCompanyId(companyId);
            if (offers.size() == 0) {
                throw new IllegalArgumentException("No such company has made offers!");
            }

        return offers;
    }

    /**
     * Service, which provides Targeted offers, related to a specific Student offer.
     *
     * @param StudentOfferId - the id of the Student offer that we want to specify.
     * @return - A list of Targeted requests, which satisfy our condition.
     */
    public List<TargetedCompanyOffer> getOffersByStudentOffer(Long StudentOfferId) {
        StudentOffer studentOffer = studentOfferRepository.getById(StudentOfferId);
        if(studentOffer == null) {
            throw new IllegalArgumentException("Student offer does not exist");
        }
        List<TargetedCompanyOffer> offers = targetedCompanyOfferRepository.findAllByStudentOffer(studentOffer);
        if (offers.size() == 0) {
            throw new IllegalArgumentException("No such company has made offers!");
        }
        return offers;
    }
}
