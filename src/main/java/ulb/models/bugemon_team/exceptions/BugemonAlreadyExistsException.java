package ulb.models.bugemon_team.exceptions;

/** Thrown when trying to add a Bugemon already present in the team. */
public class BugemonAlreadyExistsException extends RuntimeException {
    public BugemonAlreadyExistsException(String message) {
        super(message);
    }
}
