package bugemon.common.models.team.exceptions;

/**
 * Thrown when attempting to add a {@link bugemon.common.models.player.PlayerBugemon} that is already a member of the
 * target {@link bugemon.common.models.team.Team} (membership is determined by name equality).
 */
public class BugemonAlreadyPresentInTeamException extends RuntimeException {
    public BugemonAlreadyPresentInTeamException(String message) {
        super(message);
    }

    public BugemonAlreadyPresentInTeamException(String message, Throwable cause) {
        super(message, cause);
    }
}
