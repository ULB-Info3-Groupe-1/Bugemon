package ulb.repositories.exceptions;

/**
 * Thrown when saving a team with an empty name
 */
public class TeamNameEmptyException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the error message
     */
    public TeamNameEmptyException(String message) {
        super(message);
    }
}
