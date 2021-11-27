package nl.tudelft.sem.template.services;

import java.util.Optional;

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
    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
