package envLoader;

import static java.util.regex.Pattern.matches;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class envLoaderParser {
    private final String directory;
    private final String filename;
    private final Function<String, Boolean> isComment = s -> s.startsWith("#");
    private final Function<String, Boolean> isWhiteSpace = s -> matches("^\\s*$", s);
    private final Function<String, Boolean> isBlank = s -> s == null || s.trim().isEmpty();
    private final Function<String, Boolean> isInvalid = s -> !s.contains("=");
    private final Function<String, Boolean> shouldNotSkip = s ->
        !isComment.apply(s) && !isBlank.apply(s) && !isWhiteSpace.apply(s) && !isInvalid.apply(s);
    private final Function<String, Boolean> isQuoted = s -> s.startsWith("\"") && s.endsWith("\"");

    public envLoaderParser(String path, String directory) {
        this.directory = path;
        this.filename = directory;
    }

    /**
     * Parses the file.
     *
     * @return A map of key value pairs for the environment variables.
     * @throws FileNotFoundException If the file does not exist.
     */
    public Map<String, String> parse() throws FileNotFoundException {
        var lines = read();

        if (lines == null) {
            return null;
        }

        Map<String, String> res = new HashMap<>();
        lines
            .stream()
            .filter(shouldNotSkip::apply)
            .forEach(line -> {
                var split = line.split("=");
                res.put(split[0], split[1]);
            });

        return res;
    }

    /**
     * Reads in the file.
     *
     * @return List of lines.
     * @throws FileNotFoundException If the file does not exist.
     */
    private List<String> read() throws FileNotFoundException {
        var location = (directory + "/" + filename).toLowerCase();
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
