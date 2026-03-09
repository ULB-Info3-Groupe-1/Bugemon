package ulb.models.bugemon_team.exceptions;

/**
 * Exception thrown when trying to remove a Bugemon although the team is already empty.
 */
public class TeamAlreadyEmptyException extends RuntimeException {
    public TeamAlreadyEmptyException(String message) {
        super(message);
    }
}
