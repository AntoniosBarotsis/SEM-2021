package nl.tudelft.sem.template.controllers;

import java.util.Optional;
import nl.tudelft.sem.template.domain.dtos.Response;
import nl.tudelft.sem.template.domain.dtos.UserCreateRequest;
import nl.tudelft.sem.template.domain.dtos.UserLoginRequest;
import nl.tudelft.sem.template.domain.dtos.UserLoginResponse;
import nl.tudelft.sem.template.entities.CompanyFactory;
import nl.tudelft.sem.template.entities.StudentFactory;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.exceptions.UserAlreadyExists;
import nl.tudelft.sem.template.exceptions.UserNotFound;
import nl.tudelft.sem.template.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private transient UserService userService;

    @Autowired
    private transient StudentFactory studentFactory;

    @Autowired
    private transient CompanyFactory companyFactory;

    /**
     * Get a user by id.
     *
     * @param username User's username.
     * @return 200 OK with the user entity if the user is found,
     *          else 404 NOT FOUND.
     */
    @GetMapping("/{username}")
    public ResponseEntity<Response<User>> getUser(@PathVariable String username) {
        try {
            return new ResponseEntity<>(new Response<>(userService.getUserOrRaise(username), null),
                    HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(new Response<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new user.
     *
     * @param userRequest User to create
     * @return 201 CREATED if the user is created,
     *          409 CONFLICT if the user already exists
     *          else 400 BAD REQUEST
     */
    @PostMapping("/")
    public ResponseEntity<Response<User>> createUser(@RequestBody UserCreateRequest userRequest) {
        try {
            User user = getUser(userRequest);

            return new ResponseEntity<>(new Response<>(userService.createUser(user), null),
                    HttpStatus.CREATED);
        } catch (UserAlreadyExists e) {
            return new ResponseEntity<>(new Response<>(null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /**
     * Update a user.
     *
     * @param userRequest User to update.
     * @return 200 OK if the user is updated,
     *          404 NOT FOUND if the user is not found
     *          else 400 BAD REQUEST
     */
    @PutMapping("/")
    public ResponseEntity<Response<User>> updateUser(@RequestBody UserCreateRequest userRequest) {
        try {
            User user = getUser(userRequest);

            return new ResponseEntity<>(new Response<>(userService.updateUser(user), null),
                    HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(new Response<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a user.
     *
     * @param username User's id.
     * @return 200 OK if the user is deleted,
     *          404 NOT FOUND if the user is not found.
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Login a user.
     *
     * @param user User ID and password to login
     * @return 200 OK with JWT token if user is logged in,
     *          else 401 UNAUTHORIZED
     */
    @PostMapping("/login")
    public ResponseEntity<Response<UserLoginResponse>> login(@RequestBody UserLoginRequest user) {
        Optional<User> u = userService.getUser(user.getUsername());
        if (u.isPresent() && userService.verifyPassword(u.get(), user.getPassword())) {
            String token = userService.generateJwtToken(u.get());
            return new ResponseEntity<>(new Response<>(new UserLoginResponse(token), null),
                    HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private User getUser(UserCreateRequest userCreateRequest) {
        switch (userCreateRequest.getRole()) {
            case STUDENT:
                return studentFactory.createUser(userCreateRequest.getUsername(),
                        userCreateRequest.getPassword());
            case COMPANY:
                return companyFactory.createUser(userCreateRequest.getUsername(),
                        userCreateRequest.getPassword());
            default:
                throw new IllegalArgumentException("Invalid role " + userCreateRequest.getRole());
        }
    }
}
