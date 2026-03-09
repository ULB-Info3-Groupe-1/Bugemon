package ulb.models.bugemon_team.exceptions;

/**
 * Exception thrown when trying to add a Bugemon although the team is already full.
 */
public class TeamAlreadyFullException extends RuntimeException {
    public TeamAlreadyFullException(String message) {
        super(message);
    }
}
