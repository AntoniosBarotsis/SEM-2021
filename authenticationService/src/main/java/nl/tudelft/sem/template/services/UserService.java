package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.Student;
import nl.tudelft.sem.template.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService implements IUserService {
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public User findByUsername(String username) {
        logger.info("Requesting User {} from UserService...", username);
        // Make a request to the appropriate endpoint

        return null;
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
}
