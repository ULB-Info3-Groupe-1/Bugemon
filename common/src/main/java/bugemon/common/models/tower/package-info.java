/**
 * Core domain model for the Tower — the roguelike structure that structures a Bugemon run.
 *
 * <p>
 * A run spans floors {@value bugemon.common.Configuration.Game#FLOOR_MIN} through
 * {@value bugemon.common.Configuration.Game#FLOOR_MAX}. Each floor is represented as a
 * {@link bugemon.common.models.tower.FloorMap}: a tree of {@link bugemon.common.models.tower.room.Room}s generated
 * procedurally from the run seed. The overall run state (seed, current floor, active team) is captured by
 * {@link bugemon.common.models.tower.TowerState}.
 *
 * <p>
 * Sub-packages:
 * <ul>
 * <li>{@code bugemon.common.models.tower.room} — room type and visited-state model</li>
 * <li>{@code bugemon.common.models.tower.reward} — reward variants awarded after combat rooms</li>
 * <li>{@code bugemon.common.models.tower.utils} — procedural floor map generation</li>
 * <li>{@code bugemon.common.models.tower.exceptions} — domain exceptions for illegal navigation</li>
 * </ul>
 */
package bugemon.common.models.tower;
