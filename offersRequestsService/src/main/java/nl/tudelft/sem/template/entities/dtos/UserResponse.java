package nl.tudelft.sem.template.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String password;
    private Role role;
}


