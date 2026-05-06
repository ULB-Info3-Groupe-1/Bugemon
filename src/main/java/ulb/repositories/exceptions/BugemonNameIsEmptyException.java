package ulb.repositories.exceptions;

/**
 * Thrown when saving a team with an empty Bugemon name.
 */
public class BugemonNameIsEmptyException extends Exception {
    public BugemonNameIsEmptyException(String message) {
        super(message);
    }

    public BugemonNameIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
