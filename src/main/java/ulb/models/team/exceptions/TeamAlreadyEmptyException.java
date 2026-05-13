package ulb.models.bugemon_team.exceptions;

/** Thrown when trying to remove a Bugemon from an already empty team. */
public class TeamAlreadyEmptyException extends RuntimeException {
    public TeamAlreadyEmptyException(String message) {
        super(message);
    }

    public TeamAlreadyEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
