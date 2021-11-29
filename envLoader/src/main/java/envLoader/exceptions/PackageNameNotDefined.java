package envLoader.exceptions;

public class PackageNameNotDefined extends RuntimeException {
    public PackageNameNotDefined() {
    }

    public PackageNameNotDefined(String message) {
        super(message);
    }
}
