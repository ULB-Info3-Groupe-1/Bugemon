package ulb.repositories.exceptions;

/**
 * Thrown when trying to create a player with a name that already exists in the database.
 */
public class PlayernameAlreadyExistsException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the error message
     */
    public PlayernameAlreadyExistsException(String message) {
        super(message);
    }

}
