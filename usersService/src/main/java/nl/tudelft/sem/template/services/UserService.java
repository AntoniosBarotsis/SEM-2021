package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private transient UserRepository userRepository;

    /** Get user by their id.
     *
     * @param userId    User Id.
     * @return          User object.
     */
    public User getUser(String userId) {
        return userRepository.getById(userId);
    }
}
