package ulb.services.exceptions;

/**
 * Thrown when saving a bugemon with a name already used by another bugemon.
 */
public class BugemonNameAlreadyExistsException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the exception message
     */
    public BugemonNameAlreadyExistsException(String message) {
        super(message);
    }
}
