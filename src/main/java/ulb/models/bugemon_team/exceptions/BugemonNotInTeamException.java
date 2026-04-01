package ulb.models.bugemon_team.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to remove or retrieve a {@link ulb.models.bugemon.Bugemon} from a
 * {@link ulb.models.bugemon_team.BugemonTeam} that does not contain a member with the requested ID.
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it in their {@code throws} clause, but they
 * should still handle it wherever operating on a Bugemon that may not be present in the team is a realistic
 * possibility.
 * </p>
 *
 * @see ulb.models.bugemon_team.BugemonTeam#removeBugemon(String)
 */
public class BugemonNotInTeamException extends RuntimeException {
    /**
     * Constructs a new {@code BugemonNotInTeamException} with the specified detail message.
     *
     * @param message
     *            a human-readable description of the violation; typically includes the ID of the Bugemon that was not
     *            found in the team.
     */
    public BugemonNotInTeamException(String message) {
        super(message);
    }
}
