package nl.tudelft.sem.template.domain.valueObjects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import nl.tudelft.sem.template.domain.exceptions.InvalidUsernameException;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Username {
    String username;

    /**
     * Creates a Username if the input is valid.
     *
     * @param username The username
     * @return The newly created Username
     * @exception InvalidUsernameException When the username is invalid
     */
    public static Username create(String username)  {
        try {
            verify(username);

            return new Username(username);
        } catch (InvalidUsernameException e) {
            throw new InvalidUsernameException(e.getMessage());
        }
    }

    /**
     * Ensures that the <code>username</code> has certain properties.
     *
     * <ul>
     *     <li>A length of 2 or more characters</li>
     * </ul>
     */
    static void verify(String username) {
        final int PasswordMinLength = 2;

        // Username cannot be null
        if (username == null) {
            throw new InvalidUsernameException("Username cannot be null");
        }

        // Check character length
        if (username.length() < PasswordMinLength) {
            final String message = "The username cannot be less than "
                + PasswordMinLength
                + " characters long";

            throw new InvalidUsernameException(message);
        }
    }
}
