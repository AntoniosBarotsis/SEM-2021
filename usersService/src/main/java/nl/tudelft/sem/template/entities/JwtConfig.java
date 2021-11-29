package nl.tudelft.sem.template.entities;

import envLoader.envLoaderBuilder;
import java.io.FileNotFoundException;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {
    private final String JwtSecret;

    public JwtConfig() throws FileNotFoundException {
        var loader = new envLoaderBuilder()
            .packageName("usersService")
            .load();

        JwtSecret = loader.get("JWT_SECRET", "secret");
    }
}
