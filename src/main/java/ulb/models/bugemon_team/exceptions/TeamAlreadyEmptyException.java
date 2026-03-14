package ulb.models.bugemon_team.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to remove a
 * {@link ulb.models.bugemon.Bugemon} from a
 * {@link ulb.models.bugemon_team.BugemonTeam} that contains no members.
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it
 * in their {@code throws} clause, but they should still handle it wherever
 * removing from a potentially empty team is a realistic possibility.
 * </p>
 *
 * @see ulb.models.bugemon_team.BugemonTeam#removeBugemon(String)
 */
public class TeamAlreadyEmptyException extends RuntimeException {
    /**
     * Constructs a new {@code TeamAlreadyEmptyException} with the specified
     * detail message.
     *
     * @param message a human-readable description of the violation; typically
     *                indicates that the team is already empty and no member
     *                can be removed from it.
     */
    public TeamAlreadyEmptyException(String message) {
        super(message);
    }
}
