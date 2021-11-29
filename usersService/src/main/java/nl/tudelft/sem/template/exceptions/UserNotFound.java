package nl.tudelft.sem.template.exceptions;

import nl.tudelft.sem.template.entities.User;

public class UserNotFound extends Exception {
    public UserNotFound(String userId) {
        super("Could not find user with ID " + userId);
    }
}
