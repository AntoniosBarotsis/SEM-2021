package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.tudelft.sem.template.entities.JwtConfig;
import nl.tudelft.sem.template.entities.StudentFactory;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;




@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

    @Autowired
    private transient AuthService authService;

    private transient User user;

    @BeforeEach
    void setUp() {
        user = new StudentFactory().createUser("testing", "testPass");
    }

    @Test
    void testHashPassword() {
        String password = "test";
        String hashedPassword = authService.hashPassword(password);
        assertNotEquals(password, hashedPassword);
    }

    @Test
    void testVerifyPassword() {
        String password = "test_password";
        User hashedPasswordUser = new StudentFactory()
                .createUser("test2", authService.hashPassword(password));

        assertTrue(authService.verifyPassword(hashedPasswordUser, password));
        assertFalse(authService.verifyPassword(hashedPasswordUser, "different_test_password"));
    }

    @Test
    void testGenerateJwtToken() {
        String token = authService.generateJwtToken(user);
        assertNotNull(token);
        DecodedJWT decodedToken = JWT.decode(token);
        assertEquals(user.getUsername(), decodedToken.getClaim("userName").asString());
        assertEquals(user.getRole().toString(), decodedToken.getClaim("userRole").asString());
    }

    @Test
    void testDefaultAdminPassword() {
        String authPassword = authService.getAdminPassword();
        assertNotNull(authPassword);
    }
}
