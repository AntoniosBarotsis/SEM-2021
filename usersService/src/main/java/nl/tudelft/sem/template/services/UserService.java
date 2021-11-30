package nl.tudelft.sem.template.services;

import java.util.Optional;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.exceptions.UserAlreadyExists;
import nl.tudelft.sem.template.exceptions.UserNotFound;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private transient UserRepository userRepository;

    /**
     * Get user by their id.
     *
     * @param username User's username.
     * @return User object.
     */
    public Optional<User> getUser(String username) {
        return userRepository.findById(username);
    }

    /**
     * Get user by their id, throws UserNotFound if user is not found.
     *
     * @param username - the username.
     * @return User object.
     * @throws UserNotFound if user is not found.
     */
    public User getUserOrRaise(String username) throws UserNotFound {
        Optional<User> u = userRepository.findById(username);
        if (u.isPresent()) {
            return u.get();
        }
        throw new UserNotFound(username);
    }

    /**
     * Create a new user.
     *
     * @param user User to create.
     * @return User object.
     * @throws UserAlreadyExists if a user with same id already exists.
     */
    public User createUser(User user) throws UserAlreadyExists {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExists(user);
        }
        return userRepository.save(user);
    }

    /**
     * Update a user.
     *
     * @param user User to update.
     * @return User object.
     * @throws UserNotFound if user is not found.
     */
    public User updateUser(User user) throws UserNotFound {
        if (userRepository.existsByUsername(user.getUsername())) {
            return userRepository.save(user);
        }

        throw new UserNotFound(user.getUsername());

    }

    /**
     * Deletes a user.
     *
     * @param username - the username of the User.
     * @throws UserNotFound - is thrown if such a User doesn't exist.
     */
    public void deleteUser(String username) throws UserNotFound {
        getUserOrRaise(username);
        userRepository.deleteById(username);
    }
}
