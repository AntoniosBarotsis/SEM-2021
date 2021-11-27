package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private transient UserService userService;

    /** Get a user by id.
     *
     * @param id   User's id.
     * @return     200 OK with the user entity if the user is found,
     *             else 404 NOT FOUND
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getUser(@PathVariable String id) {
        Optional<User> u = userService.getUser(id);
        if (u.isPresent()) {
            return new ResponseEntity<>(u.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Create a new user
     *
     * @param user User to create
     * @return 201 CREATED if the user is created,
     *         else 400 BAD REQUEST
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
