/**
 * Team management model for the Bugemon game.
 *
 * <p>
 * {@link ulb.models.team.Team} is an ordered collection of up to {@value ulb.Configuration.Game#MAX_TEAM_SIZE}
 * {@link ulb.models.bugemon.Bugemon}s with membership uniqueness enforced by name. Constraint violations throw
 * unchecked exceptions defined in the {@code ulb.models.team.exceptions} sub-package.
 *
 * <p>
 * Factory classes for building NPC or random teams live in the {@code ulb.models.team.factory} sub-package.
 */
package ulb.models.team;
