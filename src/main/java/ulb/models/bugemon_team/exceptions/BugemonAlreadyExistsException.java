/**
 * File name : BugemonAlreadyExistsException.java
 * Description : Exception thrown when trying to add a duplicate Bugemon to a team.
 *
 * @author Brisbois Philippe
 * @date 27 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon_team.exceptions;

/**
 * Unchecked exception thrown when an attempt is made to add a {@code Bugemon} to a
 * {@link ulb.models.bugemon_team.BugemonTeam} that already contains a member with the same ID.
 *
 * <p>
 * Because a team must hold at most one instance of each Bugemon (identified by its unique string ID),
 * {@link ulb.models.bugemon_team.BugemonTeam#addBugemon} raises this exception rather than silently ignoring or
 * overwriting the duplicate entry.
 * </p>
 *
 * <p>
 * This is a {@link RuntimeException} so callers are not forced to declare it in their {@code throws} clause, but they
 * should still handle it wherever duplicate additions are a realistic possibility.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * try {
 *     team.addBugemon(bugemon);
 * } catch (BugemonAlreadyExistsException e) {
 *     // inform the user that the Bugemon is already in the team
 * }
 * }</pre>
 *
 * @see ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)
 */
public class BugemonAlreadyExistsException extends RuntimeException {
    /**
     * Constructs a new {@code BugemonAlreadyExistsException} with the specified detail message.
     *
     * @param message
     *            a human-readable description of the duplication conflict; typically includes the ID of the Bugemon
     *            that was already present in the team.
     */
    public BugemonAlreadyExistsException(String message) {
        super(message);
    }
}
