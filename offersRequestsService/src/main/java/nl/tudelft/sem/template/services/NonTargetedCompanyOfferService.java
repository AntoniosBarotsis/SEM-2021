package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.NonTargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.NonTargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NonTargetedCompanyOfferService {

    private static final transient double MAX_HOURS = 20;
    private static final transient double  MAX_WEEKS = 26;

    @Autowired
    private NonTargetedCompanyOfferRepository nonTargetedCompanyOfferRepository;

    /**
     * Method for saving offers.
     *
     * @param offer     Non-Targeted-company-offer that needs to be saved.
     * @return          Saved Offer.
     * @throws IllegalArgumentException Thrown when the offer is not valid
     *                                  e.g. exceeds 20 hours per week or 6 month duration
     */
    public NonTargetedCompanyOffer saveOffer(NonTargetedCompanyOffer offer) throws IllegalArgumentException{
        OfferService.validateOffer(offer);

        offer.setStatus(Status.PENDING);
        return nonTargetedCompanyOfferRepository.save(offer);
    }
}
