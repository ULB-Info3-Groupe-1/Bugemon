package ulb.repositories.exceptions;

/**
 * Thrown when trying to save an empty team.
 */
public class TeamEmptyException extends Exception {
    public TeamEmptyException(String message) {
        super(message);
    }

    public TeamEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
