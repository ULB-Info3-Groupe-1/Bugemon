package ulb.repositories.exceptions;

/** Thrown when saving a Bugemon with a blank or null name. */
public class BugemonNameIsEmptyException extends Exception {
    public BugemonNameIsEmptyException(String message) {
        super(message);
    }
}
