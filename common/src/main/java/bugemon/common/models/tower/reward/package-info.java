/**
 * Reward model for the Bugemon Tower.
 *
 * <p>
 * After completing a combat room, the player is offered a choice of three rewards, one of each concrete type:
 *
 * <ul>
 * <li>{@link bugemon.common.models.tower.reward.AttackReward} — unlocks a new attack for a team member.</li>
 * <li>{@link bugemon.common.models.tower.reward.BonusStatsReward} — applies a permanent stat bonus to a team member.</li>
 * <li>{@link bugemon.common.models.tower.reward.ItemReward} — adds a quantity of a consumable item to the player's inventory.</li>
 * </ul>
 *
 * <p>
 * The marker interface {@link bugemon.common.models.tower.reward.Reward} is the common type used by controllers.
 * {@link bugemon.common.models.tower.reward.RewardGenerator} builds the triplet for a given floor, biasing attack rewards toward
 * types compatible with the current team.
 */
package bugemon.common.models.tower.reward;
