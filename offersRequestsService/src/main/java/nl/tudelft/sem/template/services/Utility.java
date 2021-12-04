package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.dtos.UserResponseWrapper;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class Utility {
    /**
     * Checks to see if the given user exists.
     * 
     * @param userId The user id.
     * @param restTemplate The rest template singleton.
     */
    static void userExists(String userId, RestTemplate restTemplate) {
        try {
            var url = "http://users-service/" + userId;
            var res = restTemplate.getForObject(url, UserResponseWrapper.class);

            if (res == null) {
                throw new UserServiceUnvanvailableException();
            }

            if (res.getData() == null) {
                throw new UserDoesNotExistException(res.getErrorMessage());
            }
        } catch (RestClientException e) {
            throw new UserServiceUnvanvailableException(e.getMessage());
        }
    }
}
