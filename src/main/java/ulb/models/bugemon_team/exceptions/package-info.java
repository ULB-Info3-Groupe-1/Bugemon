/**
 * Contains exception classes thrown by {@link ulb.models.bugemon_team.BugemonTeam} when
 * team-management constraints are violated.
 *
 * <h2>Package overview</h2>
 * <p>
 * Every exception in this package is an unchecked {@link java.lang.RuntimeException}, so callers
 * are not forced to declare them in a {@code throws} clause. They are, however, part of the
 * documented contract of the methods that raise them and should be caught wherever the triggering
 * condition is a realistic possibility.
 * </p>
 *
 * <h2>Exceptions</h2>
 * <ul>
 * <li>{@link ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException} — thrown by
 * {@link ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)} when a
 * {@link ulb.models.bugemon.Bugemon} with the same ID is already a member of the team.</li>
 * <li>{@link ulb.models.bugemon_team.exceptions.TeamAlreadyFullException} — thrown by
 * {@link ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)} when the team
 * has already reached its maximum capacity of six members and no further Bugemon can be added.</li>
 * <li>{@link ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException} — thrown by
 * {@link ulb.models.bugemon_team.BugemonTeam#removeBugemon(String)} when an attempt is made to
 * remove a Bugemon from a team that contains no members.</li>
 * <li>{@link ulb.models.bugemon_team.exceptions.BugemonNotInTeamException} — thrown by
 * {@link ulb.models.bugemon_team.BugemonTeam#removeBugemon(String)} when no Bugemon with the
 * requested ID exists in the team.</li>
 * </ul>
 *
 * @see ulb.models.bugemon_team.BugemonTeam
 * @see ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException
 * @see ulb.models.bugemon_team.exceptions.TeamAlreadyFullException
 * @see ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException
 * @see ulb.models.bugemon_team.exceptions.BugemonNotInTeamException
 */
package ulb.models.bugemon_team.exceptions;
