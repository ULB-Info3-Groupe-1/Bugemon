package ulb.models.team.exceptions;

/**
 * Thrown when attempting to access or remove a {@link ulb.models.player.PlayerBugemon} that is not currently a member
 * of the target {@link ulb.models.team.Team}.
 */
public class BugemonNotInTeamException extends RuntimeException {
    public BugemonNotInTeamException(String message) {
        super(message);
    }

    public BugemonNotInTeamException(String message, Throwable cause) {
        super(message, cause);
    }
}
