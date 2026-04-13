package ulb.repositories.exceptions;

/**
 * Thrown when saving a bugemon with a name already used by another bugemon.
 */
public class BugemonNameAlreadyExistsException extends Exception {
    public BugemonNameAlreadyExistsException(String message) {
        super(message);
    }
}
