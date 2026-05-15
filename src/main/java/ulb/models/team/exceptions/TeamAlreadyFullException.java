package ulb.models.team.exceptions;

/** Thrown when trying to add a Bugemon to a team already at maximum capacity. */
public class TeamAlreadyFullException extends RuntimeException {
    public TeamAlreadyFullException(String message) {
        super(message);
    }

    public TeamAlreadyFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
