/**
 * Data-access layer: repository interfaces that abstract all persistence operations for the game.
 *
 * <p>
 * On startup, {@link ulb.repositories.QueryLoader} scans {@code resources/sql/*.sql} and builds an in-memory map of
 * named SQL strings. Concrete repository implementations (found in {@code ulb.repositories.postgres}) call
 * {@link ulb.repositories.DatabaseConnection#prepareStatement} with those strings to execute queries.
 *
 * <p>
 * Key interfaces:
 * <ul>
 * <li>{@link ulb.repositories.PlayerRepository} — player creation and tower-floor tracking</li>
 * <li>{@link ulb.repositories.BugemonRepository} — per-player Bugemon ownership</li>
 * <li>{@link ulb.repositories.TeamRepository} — named teams and active-team selection</li>
 * <li>{@link ulb.repositories.InventoryRepository} — item inventories</li>
 * <li>{@link ulb.repositories.SkillRepository} — skill-tree progress and skill points</li>
 * <li>{@link ulb.repositories.TowerRepository} — in-progress tower run snapshots</li>
 * <li>{@link ulb.repositories.StaticRepository} — immutable game data (archetypes, attacks, items)</li>
 * <li>{@link ulb.repositories.MusicRepository} — audio track lookup by scene context</li>
 * </ul>
 */
package ulb.repositories;
