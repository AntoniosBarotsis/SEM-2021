package nl.tudelft.sem.template.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.domain.valueObjects.Password;
import nl.tudelft.sem.template.domain.valueObjects.Username;

/**
 * Abstract User type that represents any type of application user.
 */
@Data
public abstract class User {
    private Username username;
    private Password password;
    private String name;

    protected User(Username username, Password password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
