package nl.tudelft.sem.template.services;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.entities.dtos.ContractDto;
import nl.tudelft.sem.template.entities.dtos.UserResponseWrapper;
import nl.tudelft.sem.template.exceptions.ContractCreationException;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
public class Utility {

    /**
     * Checks to see if the given user exists.
     *
     * @param userId       The user id.
     * @param restTemplate The rest template singleton.
     */
    public void userExists(String userId, RestTemplate restTemplate)
            throws UserDoesNotExistException, UserServiceUnvanvailableException {
        try {
            var url = "http://users-service/" + userId;
            var res = restTemplate.getForObject(url, UserResponseWrapper.class);

            if (res == null) {
                throw new UserServiceUnvanvailableException();
            }

            if (res.getData() == null) {
                throw new UserDoesNotExistException(res.getErrorMessage());
            }
        } catch (RestClientException e) {
            throw new UserServiceUnvanvailableException(e.getMessage());
        }
    }

    /**
     * Creates a contract between 2 parties by making a request to the contract microservice.
     *
     * @param companyId    The company's id.
     * @param studentId    The student's id.
     * @param hoursPerWeek How many hours per week.
     * @param totalHours   The amount of total hours.
     * @param pricePerHour The price per hour.
     * @param restTemplate The rest template singleton.
     * @return The created contract entity.
     * @throws ContractCreationException If the contract parameters were invalid
     *                                or if the request or if the contract service was unavailable.
     */
    public ContractDto createContract(String companyId, String studentId, Double hoursPerWeek,
                                      Double totalHours, Double pricePerHour,
                                      RestTemplate restTemplate)
            throws ContractCreationException {

        ContractDto sentDto
                = new ContractDto(companyId, studentId, hoursPerWeek, totalHours, pricePerHour);

        try {
            System.out.println("Sending contract to contract microservice");
            String url = "http://contract-service/";

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-user-role", "INTERNAL_SERVICE");

            HttpEntity<ContractDto> requestEntity = new HttpEntity<>(sentDto, headers);
            return restTemplate
                    .postForObject(url, requestEntity, ContractDto.class);

        } catch (RestClientException e) {
            // Error message from the contract microservice:
            throw new ContractCreationException(e.getMessage());
        }
    }
}
