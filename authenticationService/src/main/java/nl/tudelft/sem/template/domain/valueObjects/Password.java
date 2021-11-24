package nl.tudelft.sem.template.domain.valueObjects;

import javax.management.InstanceAlreadyExistsException;
import lombok.Value;
import nl.tudelft.sem.template.domain.exceptions.InvalidPasswordException;

@Value
public class Password {
    String password;

    /**
     * Creates a Password if the input is valid
     *
     * @param password The password
     * @return The newly created Password
     * @exception InvalidPasswordException When the password is invalid
     */
    public static Password create(String password)  {
        try {
            verify(password);

            return new Password(password);
        } catch (InvalidPasswordException e) {
            throw new InvalidPasswordException(e.getMessage());
        }
    }

    /**
     * Ensures that the <code>password</code> is has these properties:
     *
     * <ul>
     *     <li>A length of 6 or more characters</li>
     * </ul>
     */
    private static void verify(String password) {
        final int PASSWORD_MIN_LENGTH = 6;

        // Password cannot be null
        if (password == null) {
            throw new InvalidPasswordException("Password cannot be null");
        }

        // Check character length
        if (password.length() < PASSWORD_MIN_LENGTH) {
            final String message = "The password cannot be less than " +
                PASSWORD_MIN_LENGTH +
                "characters long";

            throw new InvalidPasswordException(message);
        }
    }
}
