package nl.tudelft.sem.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.entities.Offer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.entities.dtos.AverageRatingResponse;
import nl.tudelft.sem.template.entities.dtos.Response;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.exceptions.LowRatingException;
import nl.tudelft.sem.template.exceptions.UpstreamServiceException;
import nl.tudelft.sem.template.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Primary
public class OfferService {

    private static final transient double MAX_HOURS = 20;
    private static final transient double MAX_WEEKS = 26;
    private static final transient double MIN_RATING = 2.5;

    @Autowired
    private transient OfferRepository offerRepository;

    @Autowired
    private transient RestTemplate restTemplate;

    /**
     * Method for saving offers.
     *
     * @param offer Offer that needs to be saved.
     * @return Saved Offer.
     * @throws IllegalArgumentException Thrown when the offer is not valid
     *                                  e.g. exceeds 20 hours per week or 6 month duration
     */
    public Offer saveOffer(Offer offer) throws
            IllegalArgumentException, LowRatingException, UpstreamServiceException {
        if (offer.getHoursPerWeek() > MAX_HOURS) {
            throw new IllegalArgumentException("Offer exceeds 20 hours per week");
        }
        if (offer.getTotalHours() / offer.getHoursPerWeek() > MAX_WEEKS) {
            throw new IllegalArgumentException("Offer exceeds 6 month duration");
        }

        // Contact the user feedback service to get the average rating.
        double rating = getAverageRating(offer.getCreatorUsername());
        if (rating < MIN_RATING && rating != 0) {
            throw new LowRatingException("create offer", MIN_RATING);
        }

        offer.setStatus(Status.PENDING);
        return offerRepository.save(offer);
    }

    /**
     * Calls saveOffer, but instead of throwing exceptions, it returns a ResponseEntity.
     * @param offer Offer that needs to be saved.
     * @return ResponseEntity with the saved Offer, or thrown exception.
     */
    public ResponseEntity<Response<Offer>> saveOfferWithResponse(Offer offer) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(saveOffer(offer)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(null, e.getMessage()));
        } catch (LowRatingException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(null, e.getMessage()));
        } catch (UpstreamServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new Response<>(null, e.getMessage()));
        }
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

    public double getAverageRating(String username) throws UpstreamServiceException{
        String feedbackServiceUrl = "http://feedback-service/user/" + username;
        try {
            AverageRatingResponse response = restTemplate.getForObject(
                    feedbackServiceUrl, AverageRatingResponse.class
            );
            assert response != null;
            return response.getAverageRating();
        } catch (Exception exception) {
            throw new UpstreamServiceException(
                    "Unable to get an average rating for " + username + " from feedback service.",
                    exception
            );
        }
    }
}

