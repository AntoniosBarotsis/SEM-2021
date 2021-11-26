package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private transient UserService userService;

    /** Get a user by id.
     *
     * @param id   User's id.
     * @return     302 FOUND with the user entity if the user is found,
     *             else 404 NOT FOUND
     */
    @GetMapping("/getUsers/{id}")
    @ResponseBody
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
