package nl.tudelft.sem.template.domain;

import lombok.EqualsAndHashCode;
import nl.tudelft.sem.template.domain.exceptions.InvalidPasswordException;
import nl.tudelft.sem.template.domain.exceptions.InvalidUsernameException;
import nl.tudelft.sem.template.domain.valueObjects.Password;
import nl.tudelft.sem.template.domain.valueObjects.Username;

@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    private Student(Username username, Password password, String name) {
        super(username, password, name);
    }

    /**
     * Creates a user if the username and password are valid.
     *
     * @param username The username
     * @param password The password
     * @param name The name
     * @return User
     * @exception  IllegalArgumentException If either the username or password are invalid
     */
    public static Student create(String username, String password, String name) {
        try {
            var actualUsername = Username.create(username);
            var actualPassword = Password.create(password);

            return new Student(actualUsername, actualPassword, name);
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
