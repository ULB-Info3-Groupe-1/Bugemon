/**
 * Data-access layer: repository interfaces that abstract all persistence operations for the game.
 *
 * <p>
 * On startup, {@link bugemon.server.repositories.QueryLoader} scans {@code resources/sql/*.sql} and builds an in-memory
 * map of named SQL strings. Concrete repository implementations (found in {@code bugemon.server.repositories.postgres})
 * call {@link bugemon.server.repositories.DatabaseConnection#prepareStatement} with those strings to execute queries.
 *
 * <p>
 * Key interfaces:
 * <ul>
 * <li>{@link bugemon.server.repositories.PlayerRepository} — player creation and tower-floor tracking</li>
 * <li>{@link bugemon.server.repositories.BugemonRepository} — per-player Bugemon ownership</li>
 * <li>{@link bugemon.server.repositories.TeamRepository} — named teams and active-team selection</li>
 * <li>{@link bugemon.server.repositories.InventoryRepository} — item inventories</li>
 * <li>{@link bugemon.server.repositories.SkillRepository} — skill-tree progress and skill points</li>
 * <li>{@link bugemon.server.repositories.TowerRepository} — in-progress tower run snapshots</li>
 * <li>{@link bugemon.server.repositories.StaticRepository} — immutable game data (archetypes, attacks, items)</li>
 * <li>{@link bugemon.client.repositories.MusicRepository} — audio track lookup by scene context</li>
 * </ul>
 */
package bugemon.server.repositories;
