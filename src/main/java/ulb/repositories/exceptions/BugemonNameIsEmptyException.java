package ulb.repositories.exceptions;

/**
 * Thrown when saving a team with an empty Bugemon name.
 */
public class BugemonNameIsEmptyException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the error message
     */
    public BugemonNameIsEmptyException(String message) {
        super(message);
    }
}
