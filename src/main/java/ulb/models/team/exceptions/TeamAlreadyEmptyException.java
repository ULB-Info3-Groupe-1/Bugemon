package ulb.models.team.exceptions;

/**
 * Thrown when attempting to remove a member from a {@link ulb.models.team.Team} that is already empty.
 */
public class TeamAlreadyEmptyException extends RuntimeException {
    public TeamAlreadyEmptyException(String message) {
        super(message);
    }

    public TeamAlreadyEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
