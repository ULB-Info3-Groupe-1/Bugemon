package ulb.services.exceptions;

/**
 * Thrown when the player does not have an active team.
 */
public class NoActiveTeamException extends IllegalStateException {
    public NoActiveTeamException(String message) {
        super(message);
    }

    public NoActiveTeamException(String message, Throwable cause) {
        super(message, cause);
    }
}
