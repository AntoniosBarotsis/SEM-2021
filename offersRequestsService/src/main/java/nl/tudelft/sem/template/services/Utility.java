package nl.tudelft.sem.template.services;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.entities.dtos.ContractDTO;
import nl.tudelft.sem.template.entities.dtos.UserResponseWrapper;
import nl.tudelft.sem.template.exceptions.ContractCreationException;
import nl.tudelft.sem.template.exceptions.UserDoesNotExistException;
import nl.tudelft.sem.template.exceptions.UserServiceUnvanvailableException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
    public void userExists(String userId, RestTemplate restTemplate) {
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
    public ContractDTO createContract(String companyId, String studentId, Double hoursPerWeek,
                                      Double totalHours, Double pricePerHour, RestTemplate restTemplate)
            throws ContractCreationException {

        ContractDTO sentDTO
                = new ContractDTO(companyId, studentId, hoursPerWeek, totalHours, pricePerHour);

        try {
            String url = "http://contract-service/";
            HttpEntity<ContractDTO> requestEntity = new HttpEntity<>(sentDTO);

            Object response = restTemplate.
                    exchange(url, HttpMethod.POST, requestEntity, Object.class)
                    .getBody();

            // Try to cast to a contract:
            try {
                return (ContractDTO) response;
            } catch (ClassCastException e) {
                // Response is not a contract, but an error message:
                String errorMsg = (String) response;
                // Throw exception with the error message:
                throw new ContractCreationException(errorMsg);
            }
        } catch (RestClientException e) {
            throw new ContractCreationException(e.getMessage());
        }
    }
}
