package ulb.models.bugemon_team.exceptions;

/**
 * Exception thrown when trying to add a Bugemon that already exists in the team.
 */
public class BugemonAlreadyExistsException extends RuntimeException {
    public BugemonAlreadyExistsException(String message) {
        super(message);
    }
}
