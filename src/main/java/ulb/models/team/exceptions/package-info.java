/**
 * Unchecked exceptions thrown by {@link ulb.models.team.Team} when team-management constraints are violated.
 *
 * <ul>
 * <li>{@link ulb.models.team.exceptions.BugemonAlreadyPresentInTeamException} — duplicate member</li>
 * <li>{@link ulb.models.team.exceptions.BugemonNotInTeamException} — member not found</li>
 * <li>{@link ulb.models.team.exceptions.TeamAlreadyEmptyException} — remove from empty team</li>
 * <li>{@link ulb.models.team.exceptions.TeamAlreadyFullException} — add to full team</li>
 * </ul>
 */
package ulb.models.team.exceptions;
