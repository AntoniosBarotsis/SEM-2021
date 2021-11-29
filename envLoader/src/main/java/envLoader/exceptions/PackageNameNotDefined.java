package envLoader.exceptions;

import java.io.Serial;

public class PackageNameNotDefined extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PackageNameNotDefined() {
    }

    public PackageNameNotDefined(String message) {
        super(message);
    }
}
