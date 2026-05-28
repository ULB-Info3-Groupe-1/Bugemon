package bugemon.common.models.team.exceptions;

/**
 * Thrown when attempting to add a member to a {@link bugemon.common.models.team.Team} that has already reached its maximum
 * capacity of {@value bugemon.common.Configuration.Game#MAX_TEAM_SIZE} members.
 */
public class TeamAlreadyFullException extends RuntimeException {
    public TeamAlreadyFullException(String message) {
        super(message);
    }

    public TeamAlreadyFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
