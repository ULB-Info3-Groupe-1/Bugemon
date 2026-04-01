package ulb.services.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to save a team with a name that already exists
 * for the current user.
 *
 * <p>
 * Because each user's teams must have unique names, {@link ulb.services.PlayerService#saveTeam}
 * raises this exception rather than silently overwriting an existing team or creating a duplicate
 * entry.
 * </p>
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it in their
 * {@code throws} clause, but they should still handle it wherever duplicate team names are a
 * realistic possibility (e.g., when users are creating or renaming teams).
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * try {
 *     playerService.saveTeam(teamName, team);
 * } catch (TeamNameAlreadyExistsException e) {
 *     // inform the user that the team name is already in use
 * }
 * }</pre>
 *
 * @see ulb.services.PlayerService#saveTeam(String, ulb.models.bugemon_team.BugemonTeam)
 */
public class TeamNameAlreadyExistsException extends Exception {
    /**
     * Constructs a new {@code TeamNameAlreadyExistsException} with the specified detail message.
     *
     * @param message
     *            a human-readable description of the duplication conflict; typically includes the
     *            team name that already exists.
     */
    public TeamNameAlreadyExistsException(String message) {
        super(message);
    }
}
