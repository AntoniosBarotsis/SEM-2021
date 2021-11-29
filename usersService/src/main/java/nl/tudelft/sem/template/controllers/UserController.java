package nl.tudelft.sem.template.controllers;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.exceptions.UserAlreadyExists;
import nl.tudelft.sem.template.exceptions.UserNotFound;
import nl.tudelft.sem.template.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
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
        try {
            return new ResponseEntity<>(userService.getUserOrRaise(id), HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /** Create a new user
     *
     * @param user User to create
     * @return 201 CREATED if the user is created,
     *         409 CONFLICT if the user already exists
     *         else 400 BAD REQUEST
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
        } catch (UserAlreadyExists e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /** Update a user
     *
     * @param user User to update
     * @return 200 OK if the user is updated,
     *         404 NOT FOUND if the user is not found
     *         else 400 BAD REQUEST
     */
    @PutMapping("/")
    @ResponseBody
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /** Delete a user
     *
     * @param id User's id
     * @return 200 OK if the user is deleted,
     *         404 NOT FOUND if the user is not found
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
