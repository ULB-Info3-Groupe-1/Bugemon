package ulb.models.bugemon_team.exceptions;

/** Thrown when trying to access or remove a Bugemon not present in the team. */
public class BugemonNotInTeamException extends RuntimeException {
    public BugemonNotInTeamException(String message) {
        super(message);
    }

    public BugemonNotInTeamException(String message, Throwable cause) {
        super(message, cause);
    }
}
