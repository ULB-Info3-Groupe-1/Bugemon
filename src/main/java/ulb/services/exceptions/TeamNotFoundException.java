package ulb.services.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to load a team that does not exist for the current user.
 *
 * <p>
 * When {@link ulb.services.PlayerService#loadTeamAndSetActiveTeam} is called with a team name that doesn't correspond
 * to any of the user's saved teams, this exception is raised to indicate the operation cannot proceed.
 * </p>
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it in their {@code throws} clause, but they
 * should still handle it wherever loading non-existent teams is a realistic possibility (e.g., when users manually
 * enter team names to load).
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * try {
 *     playerService.loadTeamAndSetActiveTeam(teamName);
 * } catch (TeamNotFoundException e) {
 *     // inform the user that the team could not be found
 * }
 * }</pre>
 *
 * @see ulb.services.PlayerService#loadTeamAndSetActiveTeam(String)
 */
public class TeamNotFoundException extends Exception {
    /**
     * Constructs a new {@code TeamNotFoundException} with the specified detail message.
     *
     * @param message
     *            a human-readable description of the error; typically includes the team name that was not found.
     */
    public TeamNotFoundException(String message) {
        super(message);
    }
}
