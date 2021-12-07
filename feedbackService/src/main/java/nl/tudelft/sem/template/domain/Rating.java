package nl.tudelft.sem.template.domain;

import lombok.Data;
import nl.tudelft.sem.template.exceptions.InvalidRatingException;

@Data
public class Rating {
    private int stars;

    public Rating(int stars) {
        if (stars < 0 || stars > 5) {
            throw new InvalidRatingException();
        }

        this.stars = stars;
    }
}
