package ulb.repositories.exceptions;

/** Thrown when saving a Bugemon whose name is already used by another Bugemon. */
public class BugemonNameAlreadyExistsException extends Exception {
    public BugemonNameAlreadyExistsException(String message) {
        super(message);
    }
}
