package nl.tudelft.sem.template.controllers;


import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.User;
import nl.tudelft.sem.template.services.IUserService;
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
    private final IUserService userService;

    @Autowired
    public AuthenticationController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("public/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }

    @PostMapping("public/student/create")
    public ResponseEntity<User> createUser(@RequestBody StudentRequest student) {
        var res = userService.createStudent(student);

        if (res == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(res);
    }
}
