package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService implements IuserService {
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Student createStudent(StudentRequest student) {
        try {
            // Should send the created student to the users service eventually

            return Student.create(student.getUsername(), student.getPassword(), student.getName());
        } catch (Exception e) {
            logger.warn("Could not create student {}. {}", student.getName(), e.getMessage());
            logger.warn("Returning hardcoded user for now. REMOVE LATER");

            return Student.create("Tony", "stronkpassword", "Tony");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Make a request to get the user
        /*
        var user = ;

        if (user == null) {
            var msg = "User + " + username + " + not found.";

            logger.error(msg);
            throw new UsernameNotFoundException(msg);
        }

        // Check whether the user is a student or a company and add the proper role
        var authorities = new ArrayList<>(List.of(new SimpleGrantedAuthority("user")));

        return new org.springframework.security.core.userdetails
            .User(user.getUsername(), user.getPassword(), authorities);
        */

        return null;
    }
}
