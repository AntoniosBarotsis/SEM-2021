package envloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class EnvLoaderReader {
    public List<String> read(String directory, String filename) throws FileNotFoundException {
        var location = (directory + "/" + filename).toUpperCase(Locale.ROOT);
        var path = Path.of(new File(location).getAbsolutePath());

        if (Files.exists(path)) {
            try {
                return Files.readAllLines(path);
            } catch (IOException e) {
                return null;
            }
        } else {
            throw new FileNotFoundException("Could not find file: " + path);
        }
    }
}
