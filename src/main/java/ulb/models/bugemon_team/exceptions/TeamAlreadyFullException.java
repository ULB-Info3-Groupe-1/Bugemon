package ulb.models.bugemon_team.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to add a
 * {@link ulb.models.bugemon.Bugemon} to a
 * {@link ulb.models.bugemon_team.BugemonTeam} that has already reached its
 * maximum capacity of six members.
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it
 * in their {@code throws} clause, but they should still handle it wherever
 * adding to a potentially full team is a realistic possibility.
 * </p>
 *
 * @see ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)
 */
public class TeamAlreadyFullException extends RuntimeException {
    /**
     * Constructs a new {@code TeamAlreadyFullException} with the specified
     * detail message.
     *
     * @param message a human-readable description of the violation; typically
     *                indicates that the team has already reached its maximum
     *                size and no further members can be added.
     */
    public TeamAlreadyFullException(String message) {
        super(message);
    }
}
