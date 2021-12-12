package nl.tudelft.sem.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import logger.FileLogger;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OfferService {

    private static final transient double MAX_HOURS = 20;
    private static final transient double  MAX_WEEKS = 26;

    @Autowired
    private transient OfferRepository offerRepository;

    @Autowired
    private transient FileLogger logger;

    /**
     * Method for saving offers.
     *
     * @param offer Offer that needs to be saved.
     * @return Saved Offer.
     * @throws IllegalArgumentException Thrown when the offer is not valid
     *                                  e.g. exceeds 20 hours per week or 6 month duration
     */
    public Offer saveOffer(Offer offer) throws IllegalArgumentException {
        if (offer.getHoursPerWeek() > MAX_HOURS) {
            throw new IllegalArgumentException("Offer exceeds 20 hours per week");
        }
        if (offer.getTotalHours() / offer.getHoursPerWeek() > MAX_WEEKS) {
            throw new IllegalArgumentException("Offer exceeds 6 month duration");
        }
        offer.setStatus(Status.PENDING);
        offer = offerRepository.save(offer);
        logger.log(offer.getClass().getSimpleName()
                + " " + offer.getId()
                + " saved by user "
                + offer.getCreator());
        return offer;
    }

    /** Method for getting all Offers of a user.
     *
     * @param username String with the username.
     * @return Map with all Offers mapped to a string with their class instance.
     */
    public Map<String, List<Offer>> getAllByUsername(String username) {
        List<Offer> offers = offerRepository.getAllByUsername(username);
        Map<String, List<Offer>> res = offers.stream()
            .map(this::getClassTag)
            .distinct()
            .collect(Collectors.toMap(Function.identity(), x -> new ArrayList<>()));
        offers.forEach(x -> res.get(getClassTag(x)).add(x));
        return res;
    }

    /** Method for transforming class instance to camelCase.
     *  e.g. StudentOffer -> studentOffers.
     *
     * @param item Item we want the name of.
     * @param <T> Item can be of any class type.
     * @return String with the class instance in camelcase with s appended.
     */
    private <T> String getClassTag(T item) {
        String s = item.getClass().getSimpleName();
        return s.substring(0, 1).toLowerCase(Locale.ROOT)
            + s.substring(1, s.length())
            + "s";
    }
}

