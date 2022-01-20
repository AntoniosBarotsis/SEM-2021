package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.dtos.UserResponse;
import nl.tudelft.sem.template.entities.dtos.UserResponseWrapper;
import nl.tudelft.sem.template.entities.dtos.Role;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class UtilityTest {

    @Autowired
    Utility utility;

    @MockBean
    RestTemplate restTemplate;

    private String studentId;
    private String errorMessage;

    @BeforeEach
    void setUp() {
        studentId = "NewStudent";
        errorMessage = "There was an error";
    }

    @Test
    void userExistsTest() throws UserServiceUnvanvailableException, UserDoesNotExistException {
        Mockito
                .when(restTemplate
                        .getForObject("http://users-service/"+studentId, UserResponseWrapper.class))
                .thenReturn(new UserResponseWrapper
                        (new UserResponse(studentId, "pass", Role.STUDENT), null));

        utility.userExists(studentId);

        Mockito
                .verify(restTemplate, Mockito.times(1))
                .getForObject("http://users-service/"+studentId, UserResponseWrapper.class);
    }

    @Test
    void userExistsTestFailNull() {
        Mockito
                .when(restTemplate
                        .getForObject("http://users-service/"+studentId, UserResponseWrapper.class))
                .thenReturn(null);

        assertThrows(UserServiceUnvanvailableException.class,
                () -> utility.userExists(studentId));
    }

    @Test
    void userExistsTestFailNullData() {
        Mockito
                .when(restTemplate
                        .getForObject("http://users-service/"+studentId, UserResponseWrapper.class))
                .thenReturn(new UserResponseWrapper
                        (null, errorMessage));

        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> utility.userExists(studentId));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void userExistsTestFailRestTemplateError() {
        Mockito
                .when(restTemplate
                        .getForObject("http://users-service/"+studentId, UserResponseWrapper.class))
                .thenThrow(new RestClientException(errorMessage));

        UserServiceUnvanvailableException exception =
                assertThrows(UserServiceUnvanvailableException.class,
                () -> utility.userExists(studentId));

        assertEquals(errorMessage, exception.getMessage());
    }

}
