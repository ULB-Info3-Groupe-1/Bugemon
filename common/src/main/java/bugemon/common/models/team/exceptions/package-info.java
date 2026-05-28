/**
 * Unchecked exceptions thrown by {@link bugemon.common.models.team.Team} when team-management constraints are violated.
 *
 * <ul>
 * <li>{@link bugemon.common.models.team.exceptions.BugemonAlreadyPresentInTeamException} — duplicate member</li>
 * <li>{@link bugemon.common.models.team.exceptions.BugemonNotInTeamException} — member not found</li>
 * <li>{@link bugemon.common.models.team.exceptions.TeamAlreadyEmptyException} — remove from empty team</li>
 * <li>{@link bugemon.common.models.team.exceptions.TeamAlreadyFullException} — add to full team</li>
 * </ul>
 */
package bugemon.common.models.team.exceptions;
