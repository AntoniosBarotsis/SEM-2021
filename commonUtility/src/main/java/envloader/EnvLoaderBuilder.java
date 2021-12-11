package envloader;

import envloader.exceptions.PackageNameNotDefined;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * The <code>EnvLoader</code> builder class.
 *
 * <p>
 * Example usage:
 *
 * Say you have a `.env` file in your <code>resources</code> folder with the following contents;<br>
 * <br>
 * <code>key=value</code>
 * <br><br>
 * This is how you would read the <code>key</code> variable.
 *
 * <pre>
 *     {@code
 *var tmp = new EnvLoaderBuilder()
 *    .packageName("experimenting")
 *    .load();
 *
 *tmp.get("key") // "text"
 *tmp.get("test") // null
 *tmp.get("test", "default") // "default"
 *     }
 * </pre>
 * </p>
 */
public class EnvLoaderBuilder {
    private transient String filenameValue = ".env";
    private transient String pathValue = "src/main/resources";
    private transient String packageNameValue = null;
    private transient boolean throwFileNotFoundExceptionValue = false;

    /**
     * Sets the filename. The default is <code>.env</code>.
     *
     * @param filename The filename
     * @return EnvLoaderBuilder
     */
    public EnvLoaderBuilder filename(String filename) {
        this.filenameValue = filename;

        return this;
    }

    /**
     * Sets the path to the file. The default is <code>src/main/resources</code>.
     *
     * @param path The path
     * @return EnvLoaderBuilder
     */
    public EnvLoaderBuilder path(String path) {
        this.pathValue = path;

        return this;
    }

    /**
     * Sets the package name. This is required before <code>load()</code> is called.
     *
     * @param packageName The package name of your current project.
     * @return EnvLoaderBuilder
     */
    public EnvLoaderBuilder packageName(String packageName) {
        this.packageNameValue = packageName;

        return this;
    }

    /**
     * Makes it so the EnvLoader will throw an exception if the file is not found instead of
     * returning nulls.
     *
     * @return EnvLoaderBuilder
     */
    public EnvLoaderBuilder throwFileNotFoundException() {
        this.throwFileNotFoundExceptionValue = true;

        return this;
    }

    /**
     * Loads and parses the file.
     *
     * @return EnvLoader
     * @throws FileNotFoundException If the file cannot be found.
     */
    public EnvLoader load() throws FileNotFoundException {
        if (packageNameValue == null) {
            throw new PackageNameNotDefined();
        }

        var parser = new EnvLoaderParser(packageNameValue + "/" + pathValue, filenameValue);

        try {
            var res = parser.parse();

            return new EnvLoaderImpl(res);
        } catch (FileNotFoundException e) {
            if (throwFileNotFoundExceptionValue) {
                throw e;
            } else {
                return new EnvLoaderImpl(new HashMap<>());
            }
        }
    }
}
