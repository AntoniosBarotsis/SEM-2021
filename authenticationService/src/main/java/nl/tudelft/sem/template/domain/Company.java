package nl.tudelft.sem.template.domain;

import lombok.EqualsAndHashCode;
import nl.tudelft.sem.template.domain.valueObjects.Password;
import nl.tudelft.sem.template.domain.valueObjects.Username;

@EqualsAndHashCode(callSuper = true)
public class Company extends User {

    protected Company(Username username, Password password, String name) {
        super(username, password, name);
    }
}
