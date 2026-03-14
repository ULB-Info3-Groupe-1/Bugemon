/**
 * Provides factory classes responsible for creating domain objects used
 * in the Bugémon game.
 *
 * <p>Factories centralize object creation logic such as generating
 * Bugémon teams (e.g., random teams or starter teams) so that
 * model classes remain focused on representing data.</p>
 *
 * <h2>Key classes</h2>
 * <ul>
 *   <li>{@link ulb.factory.TeamFactory} — creates {@link ulb.models.bugemon_team.BugemonTeam}
 *       instances by randomly sampling from a pool of available
 *       {@link ulb.models.bugemon.Bugemon}s. Each selected Bugemon is deep-cloned
 *       so that the originals in the shared pool are not mutated during combat.</li>
 * </ul>
 *
 * @see ulb.models.bugemon_team.BugemonTeam
 * @see ulb.models.bugemon.Bugemon
 */
package ulb.factory;
