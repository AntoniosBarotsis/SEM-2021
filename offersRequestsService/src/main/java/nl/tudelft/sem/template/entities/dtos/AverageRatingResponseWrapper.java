package nl.tudelft.sem.template.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class AverageRatingResponseWrapper extends Response<AverageRatingResponse> {
    public AverageRatingResponseWrapper(AverageRatingResponse response, String errorMessage) {
        super(response, errorMessage);
    }
}
