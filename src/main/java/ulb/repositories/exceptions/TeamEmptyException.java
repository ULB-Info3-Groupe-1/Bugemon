package ulb.repositories.exceptions;

/**
 * Thrown when trying to save an empty team.
 */
public class TeamEmptyException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the error message
     */
    public TeamEmptyException(String message) {
        super(message);
    }
}
