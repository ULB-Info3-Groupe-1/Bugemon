/**
 * Team management model for the Bugemon game.
 *
 * <p>
 * {@link bugemon.common.models.team.Team} is an ordered collection of up to
 * {@value bugemon.common.Configuration.Game#MAX_TEAM_SIZE} {@link bugemon.common.models.bugemon.Bugemon}s with
 * membership uniqueness enforced by name. Constraint violations throw unchecked exceptions defined in the
 * {@code bugemon.common.models.team.exceptions} sub-package.
 *
 * <p>
 * Factory classes for building NPC or random teams live in the {@code bugemon.common.models.team.factory} sub-package.
 */
package bugemon.common.models.team;
