package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.NonTargetedCompanyOfferRepository;
import nl.tudelft.sem.template.repositories.OfferRepository;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

    private static final transient double MAX_HOURS = 20;
    private static final transient double  MAX_WEEKS = 26;

    @Autowired
    private transient OfferRepository offerRepository;


    /** Method for validating an offer. Checks if the offer's constraints ar met.
     *  As a separate method to avoid duplicated code.
     *
     * @param offer The offer that is validated.
     * @throws IllegalArgumentException Thrown if the offer doesn't meet the constraints
     *                                  (20h per week + 6-month contract duration)
     */
    public static void validateOffer(Offer offer) throws IllegalArgumentException{
        if (offer.getHoursPerWeek() > MAX_HOURS) {
            throw new IllegalArgumentException("Offer exceeds 20 hours per week");
        }
        if (offer.getTotalHours() / offer.getHoursPerWeek() > MAX_WEEKS) {
            throw new IllegalArgumentException("Offer exceeds 6 month duration");
        }
    }

    /**
     * Method for saving offers.
     *
     * @param offer Offer that needs to be saved.
     * @return Saved Offer.
     * @throws IllegalArgumentException Thrown when the offer is not valid
     *                                  e.g. exceeds 20 hours per week or 6 month duration
     */
    public Offer saveOffer(Offer offer) throws IllegalArgumentException {
        validateOffer(offer);

        offer.setStatus(Status.PENDING);
        return offerRepository.save(offer);
    }
}

