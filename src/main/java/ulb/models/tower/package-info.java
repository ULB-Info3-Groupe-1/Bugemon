/**
 * Core domain model for the Tower — the roguelike structure that structures a Bugemon run.
 *
 * <p>
 * A run spans floors {@value ulb.Configuration.Game#FLOOR_MIN} through {@value ulb.Configuration.Game#FLOOR_MAX}. Each
 * floor is represented as a {@link ulb.models.tower.FloorMap}: a tree of {@link ulb.models.tower.room.Room}s generated
 * procedurally from the run seed. The overall run state (seed, current floor, active team) is captured by
 * {@link ulb.models.tower.TowerState}.
 *
 * <p>
 * Sub-packages:
 * <ul>
 * <li>{@code ulb.models.tower.room} — room type and visited-state model</li>
 * <li>{@code ulb.models.tower.reward} — reward variants awarded after combat rooms</li>
 * <li>{@code ulb.models.tower.utils} — procedural floor map generation</li>
 * <li>{@code ulb.models.tower.exceptions} — domain exceptions for illegal navigation</li>
 * </ul>
 */
package ulb.models.tower;
