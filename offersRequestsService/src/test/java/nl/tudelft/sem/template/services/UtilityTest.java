package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import logger.FileLogger;
import nl.tudelft.sem.template.entities.dtos.AverageRatingResponse;
import nl.tudelft.sem.template.entities.dtos.AverageRatingResponseWrapper;
import nl.tudelft.sem.template.entities.dtos.ContractDto;
import nl.tudelft.sem.template.entities.dtos.Role;
import nl.tudelft.sem.template.entities.dtos.UserResponse;
import nl.tudelft.sem.template.entities.dtos.UserResponseWrapper;
import nl.tudelft.sem.template.exceptions.ContractCreationException;
import nl.tudelft.sem.template.exceptions.UpstreamServiceException;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
public class UtilityTest {

    @Autowired
    private transient Utility utility;

    @MockBean
    private transient RestTemplate restTemplate;
    @MockBean
    private transient FileLogger logger;

    private transient String studentId;
    private transient String errorMessage;
    private transient String companyId;
    private transient Double hoursPerWeek;
    private transient Double totalHours;
    private transient Double pricePerHour;
    private transient ContractDto contract;
    private transient HttpEntity<ContractDto> request;
    private transient String url;

    @BeforeEach
    void setUp() {
        studentId = "NewStudent";
        errorMessage = "There was an error";
        companyId = "companyId";
        hoursPerWeek = 15.0;
        totalHours = 300.0;
        pricePerHour = 15.0;
        url = "http://users-service/";

        contract =
                new ContractDto(companyId, studentId, hoursPerWeek, totalHours, pricePerHour);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-user-role", "INTERNAL_SERVICE");
        request = new HttpEntity<>(contract, headers);
    }

    @Test
    void userExistsTest() throws UserServiceUnvanvailableException, UserDoesNotExistException {
        Mockito
                .when(restTemplate
                        .getForObject(url + studentId, UserResponseWrapper.class))
                .thenReturn(new UserResponseWrapper(
                        new UserResponse(studentId, "pass", Role.STUDENT), null));

        utility.userExists(studentId);

        Mockito
                .verify(restTemplate, Mockito.times(1))
                .getForObject(url + studentId, UserResponseWrapper.class);
    }

    @Test
    void userExistsTestFailNull() {
        Mockito
                .when(restTemplate
                        .getForObject(url + studentId, UserResponseWrapper.class))
                .thenReturn(null);

        assertThrows(UserServiceUnvanvailableException.class,
                () -> utility.userExists(studentId));
    }

    @Test
    void userExistsTestFailNullData() {
        Mockito
                .when(restTemplate
                        .getForObject(url + studentId, UserResponseWrapper.class))
                .thenReturn(new UserResponseWrapper(
                        null, errorMessage));

        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class,
                () -> utility.userExists(studentId));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void userExistsTestFailRestTemplateError() {
        Mockito
                .when(restTemplate
                        .getForObject(url + studentId, UserResponseWrapper.class))
                .thenThrow(new RestClientException(errorMessage));

        UserServiceUnvanvailableException exception =
                assertThrows(UserServiceUnvanvailableException.class,
                    () -> utility.userExists(studentId));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void createContractTest() throws ContractCreationException {
        Mockito.when(restTemplate
                        .postForObject("http://contract-service/", request, ContractDto.class))
                .thenReturn(contract);
        assertEquals(contract, utility
                .createContract(companyId, studentId, hoursPerWeek, totalHours, pricePerHour));
    }

    @Test
    void createContractTestPrinting() throws ContractCreationException {
        Mockito.when(restTemplate
                        .postForObject("http://contract-service/", request, ContractDto.class))
                .thenReturn(contract);
        utility
                .createContract(companyId, studentId, hoursPerWeek, totalHours, pricePerHour);
        Mockito.verify(logger).log("Sending contract to contract microservice");
    }

    @Test
    void createContractTestFail() {
        Mockito.when(restTemplate
                        .postForObject("http://contract-service/", request, ContractDto.class))
                .thenThrow(new RestClientException(errorMessage));

        ContractCreationException exception = assertThrows(ContractCreationException.class,
                () -> utility
                        .createContract(
                                companyId, studentId, hoursPerWeek, totalHours, pricePerHour));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void getAverageRatingTestPrinting() throws UpstreamServiceException {
        Mockito
                .when(restTemplate
                        .getForObject("http://feedback-service/user/" + studentId,
                                AverageRatingResponseWrapper.class))
                .thenReturn(new AverageRatingResponseWrapper(
                        new AverageRatingResponse(4.0), null));
        utility.getAverageRating(studentId);

        Mockito.verify(logger).log("This the average rating for " + studentId + ": " + 4.0);
    }

}
