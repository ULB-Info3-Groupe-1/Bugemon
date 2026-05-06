package ulb.services.exceptions;

/**
 * Thrown when the player does not have an active team.
 */
public class NoActiveTeamException extends Exception {
    /**
     * Constructor.
     *
     * @param message
     *            the exception message
     */
    public NoActiveTeamException(String message) {
        super(message);
    }
}
