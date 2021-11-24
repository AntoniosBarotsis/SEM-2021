package nl.tudelft.sem.template.domain.valueObjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import nl.tudelft.sem.template.domain.exceptions.InvalidUsernameException;
import org.junit.jupiter.api.Test;

public class usernameTest {

    @Test
    void validUsernameTest() {
        assertThat(Username.create("tony").getUsername()).isEqualTo("tony");
    }

    @Test
    public void shortUsernameTest() {
        assertThatThrownBy(() -> Username.create("t")).isInstanceOf(InvalidUsernameException.class);
    }
}
