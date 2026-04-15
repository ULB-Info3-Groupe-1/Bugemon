package ulb.models.player.exceptions;

/**
 * Thrown when the player does not have an active team.
 */
public class NoActiveTeamException extends IllegalStateException {
    public NoActiveTeamException(String message) {
        super(message);
    }
}
