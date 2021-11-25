package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.Student;
import nl.tudelft.sem.template.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IuserService extends UserDetailsService {
    /**
     * Creates a new user in the User Service database.
     *
     * @param student The student
     * @return The newly created student
     */
    Student createStudent(StudentRequest student);
}