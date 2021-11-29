package nl.tudelft.sem;

import envLoader.envLoaderBuilder;
import java.io.FileNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {
    private final String JwtSecret;

    public JwtConfig() throws FileNotFoundException {
        var loader = new envLoaderBuilder()
            .packageName("usersService")
            .load();

        JwtSecret = loader.get("JWT_SECRET", "secret");
    }

    public String getJwtSecret() {
        return JwtSecret;
    }
}
