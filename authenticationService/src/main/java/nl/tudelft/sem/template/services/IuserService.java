package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.domain.DTOs.StudentRequest;
import nl.tudelft.sem.template.domain.Student;
import nl.tudelft.sem.template.domain.User;

public interface IUserService {
    User findByUsername(String username);
    Student createStudent(StudentRequest student);
}