package nl.tudelft.sem.template.services;

import java.util.Optional;

import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.enums.Role;
import nl.tudelft.sem.template.exceptions.UserAlreadyExists;
import nl.tudelft.sem.template.exceptions.UserNotFound;
import nl.tudelft.sem.template.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Get user by their id, throws UserNotFound if user is not found.
     * @param userId    User Id.
     * @return          User object.
     * @throws UserNotFound if user is not found.
     */
    public User getUserOrRaise(String userId) throws UserNotFound {
        Optional<User> u = userRepository.findById(userId);
        if (u.isPresent()) {
            return u.get();
        }
        throw new UserNotFound(userId);
    }

    /**
     * Create a new user.
     * @param user User to create.
     * @return User object.
     * @throws UserAlreadyExists if a user with same id already exists.
     */
    public User createUser(User user) throws UserAlreadyExists {
        if (userRepository.findById(user.getId()).isPresent()) {
            throw new UserAlreadyExists(user);
        }
        return userRepository.save(user);
    }

    /**
     * Update a user.
     * @param user User to update.
     * @return User object.
     * @throws UserNotFound if user is not found.
     */
    public User updateUser(User user) throws UserNotFound {
        if (userRepository.updateUserById(user.getId(), user.getRole(), user.getName(), user.getPassword()) > 0){
            return user;
        };
        throw new UserNotFound(user.getId());

    }

    public void deleteUser(String userId) throws UserNotFound {
        getUserOrRaise(userId);
        userRepository.deleteById(userId);
    }
}
