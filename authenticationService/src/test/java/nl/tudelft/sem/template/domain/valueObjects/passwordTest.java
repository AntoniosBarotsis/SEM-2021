package nl.tudelft.sem.template.domain.valueObjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import nl.tudelft.sem.template.domain.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.Test;

public class passwordTest {
    @Test
    void validPasswordTest() {
        assertThat(Password.create("supersecretpassword").getPassword()).isEqualTo("supersecretpassword");
    }

    @Test
    public void shortPasswordTest() {
        assertThatThrownBy(() -> Password.create("super")).isInstanceOf(InvalidPasswordException.class);
    }
}
