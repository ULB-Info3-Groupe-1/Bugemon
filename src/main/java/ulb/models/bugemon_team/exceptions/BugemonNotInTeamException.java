package ulb.models.bugemon_team.exceptions;

/**
 * Exception thrown when trying to remove a Bugemon that is not in the team.
 */
public class BugemonNotInTeamException extends RuntimeException {
    public BugemonNotInTeamException(String message) {
        super(message);
    }
}
