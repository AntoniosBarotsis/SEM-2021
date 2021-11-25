package nl.tudelft.sem.template.controllers;


import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.User;
import nl.tudelft.sem.template.services.IuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthenticationController {
    private final IuserService userService;

    @Autowired
    public AuthenticationController(IuserService userService) {
        this.userService = userService;
    }

    @GetMapping("public/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }

    /**
     * Creates a new user.
     *
     * @param student The student to create
     * @return The newly created user
     */
    @PostMapping("public/student/create")
    public ResponseEntity<User> createUser(@RequestBody StudentRequest student) {
        var res = userService.createStudent(student);

        if (res == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(res);
    }
}
