package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import nl.tudelft.sem.template.domain.dtos.Response;
import nl.tudelft.sem.template.domain.dtos.UserCreateRequest;
import nl.tudelft.sem.template.domain.dtos.UserLoginRequest;
import nl.tudelft.sem.template.domain.dtos.UserLoginResponse;
import nl.tudelft.sem.template.entities.StudentFactory;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.exceptions.UserAlreadyExists;
import nl.tudelft.sem.template.exceptions.UserNotFound;
import nl.tudelft.sem.template.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private transient UserController userController;

    @MockBean
    private transient UserService userService;

    private transient User user;
    private transient Response<User> response;

    @BeforeEach
    void setUp() {
        user = new StudentFactory().createUser("testing", "testPass");
        response = new Response<>(user, null);
    }

    @Test
    void getUserTest() throws UserNotFound {
        Mockito.when(userService.getUserOrRaise(any()))
                .thenReturn(user);

        assertEquals(response, userController.getUser(user.getUsername()).getBody());
    }

    @Test
    void getUserTestError() throws UserNotFound {
        Mockito.when(userService.getUserOrRaise(any()))
                .thenThrow(new UserNotFound(user.getUsername()));

        response.setErrorMessage("Could not find user with ID testing");
        response.setData(null);
        ResponseEntity<Response<User>> entity =
                new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        assertEquals(entity, userController.getUser("testing"));
    }

    @Test
    void createUserTest() throws UserAlreadyExists {
        Mockito.when(userService.createUser(user))
                .thenReturn(user);

        ResponseEntity<Response<User>> entity =
                new ResponseEntity<>(response, HttpStatus.CREATED);

        UserCreateRequest userRequest =
            new UserCreateRequest(user.getUsername(), user.getPassword(), user.getRole());
        assertEquals(entity, userController.createUser(userRequest));
    }

    @Test
    void createUserTestErrorAlreadyExists() throws UserAlreadyExists {
        String errorMessage = "User with id testing already exists";
        Mockito.when(userService.createUser(user))
                .thenThrow(new UserAlreadyExists(user));

        response.setData(null);
        response.setErrorMessage(errorMessage);

        ResponseEntity<Response<User>> entity =
                new ResponseEntity<>(response, HttpStatus.CONFLICT);

        UserCreateRequest userRequest =
            new UserCreateRequest(user.getUsername(), user.getPassword(), user.getRole());
        assertEquals(entity, userController.createUser(userRequest));
    }


    @Test
    void deleteUserTest() throws UserNotFound {
        Mockito.doNothing()
                .when(userService)
                .deleteUser(user.getUsername());

        ResponseEntity<String> entity =
                new ResponseEntity<>(HttpStatus.OK);
        assertEquals(entity, userController.deleteUser(user.getUsername()));
    }

    @Test
    void deleteUserTestError() throws UserNotFound {
        doThrow(new UserNotFound(user.getUsername()))
                .when(userService)
                .deleteUser(user.getUsername());

        ResponseEntity<String> entity =
                new ResponseEntity<>("Could not find user with ID testing",
                        HttpStatus.NOT_FOUND);

        assertEquals(entity, userController.deleteUser(user.getUsername()));
    }

    @Test
    void updateUserTest() throws UserNotFound {
        Mockito.when(userService.updateUser(user))
                .thenReturn(user);

        ResponseEntity<Response<User>> entity =
                new ResponseEntity<>(response, HttpStatus.OK);

        UserCreateRequest userRequest =
            new UserCreateRequest(user.getUsername(), user.getPassword(), user.getRole());
        assertEquals(entity, userController.updateUser(userRequest));
    }

    @Test
    void updateUserTestUserNotFoundTest() throws UserNotFound {
        String errorMessage = "Could not find user with ID testing";
        Mockito.when(userService.updateUser(user))
                .thenThrow(new UserNotFound(user.getUsername()));

        response.setData(null);
        response.setErrorMessage(errorMessage);

        ResponseEntity<Response<User>> entity =
                new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        UserCreateRequest userRequest =
            new UserCreateRequest(user.getUsername(), user.getPassword(), user.getRole());
        assertEquals(entity, userController.updateUser(userRequest));
    }

    @Test
    void loginUserTest() {
        Mockito.when(userService.getUser(user.getUsername()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userService.verifyPassword(user, user.getPassword())).thenReturn(true);
        Mockito.when(userService.generateJwtToken(user)).thenReturn("generated_token_test");

        ResponseEntity<Response<UserLoginResponse>> loginResponse = userController.login(
                new UserLoginRequest(user.getUsername(), user.getPassword())
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertEquals("generated_token_test", loginResponse.getBody().getData().getToken());
    }

    @Test
    void loginUserTestPasswordIncorrect() {
        Mockito.when(userService.getUser(user.getUsername()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userService.verifyPassword(user, user.getPassword())).thenReturn(true);
        Mockito.when(userService.generateJwtToken(user)).thenReturn("generated_token_test");

        ResponseEntity<Response<UserLoginResponse>> loginResponse = userController.login(
                new UserLoginRequest(user.getUsername(), "this_is_not_the_password")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }

    @Test
    void loginUserTestUserNotFound() {
        Mockito.when(userService.getUser(any())).thenReturn(java.util.Optional.empty());

        ResponseEntity<Response<UserLoginResponse>> loginResponse = userController.login(
                new UserLoginRequest(user.getUsername(), user.getPassword())
        );

        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }
}
