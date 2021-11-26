package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetedCompanyOfferService extends OfferService {

    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

    public List<TargetedCompanyOffer> getOffersById(String userId, boolean isCompany){
        List<TargetedCompanyOffer> offers;
        if(isCompany){
            offers = targetedCompanyOfferRepository.findAllByCompanyId(userId);
            if(offers.size() == 0){
                throw new IllegalArgumentException("No such company has made offers!");
            }
        }else {
            if(userId.length() != 7){
                throw new IllegalArgumentException("An invalid NetId has been entered!");
            }
            offers = targetedCompanyOfferRepository.findAllByStudentId(userId);
            if(offers.size() == 0){
                throw new IllegalArgumentException("No such student has been targeted offers!");
            }
        }
        return offers;
    }
}
