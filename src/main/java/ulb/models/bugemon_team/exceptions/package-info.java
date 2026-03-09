/**
 * Contains exception classes thrown by {@link ulb.models.bugemon_team.BugemonTeam}
 * when team-management constraints are violated.
 *
 * <h2>Package overview</h2>
 * <p>
 * Every exception in this package is an unchecked {@link java.lang.RuntimeException},
 * so callers are not forced to declare them in a {@code throws} clause. They
 * are, however, part of the documented contract of the methods that raise them
 * and should be caught wherever the triggering condition is a realistic
 * possibility.
 * </p>
 *
 * <h2>Exceptions</h2>
 * <ul>
 *   <li>{@link ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException}
 *       — thrown by
 *       {@link ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)}
 *       when a {@link ulb.models.bugemon.Bugemon} with the same ID is already a
 *       member of the team.</li>
 * </ul>
 *
 * @see ulb.models.bugemon_team.BugemonTeam
 * @see ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException
 */
package ulb.models.bugemon_team.exceptions;
