package nl.tudelft.sem.template.exceptions;

import nl.tudelft.sem.template.entities.User;

public class UserAlreadyExists extends Exception {
    public UserAlreadyExists(User user){
        super("User with id " + user.getId() + " already exists");
    }
}
