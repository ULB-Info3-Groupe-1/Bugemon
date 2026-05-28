/**
 * Strategy implementations that drive combat decision-making for both the player and AI-controlled opponents.
 *
 * <p>
 * {@link bugemon.common.models.combat.strategy.CombatStrategy} is the common interface; the three concrete implementations cover
 * every use case:
 * <ul>
 * <li>{@link bugemon.common.models.combat.strategy.PlayerStrategy} — forwards decisions to the UI via
 * {@link bugemon.common.models.player.PlayerInputHandler}</li>
 * <li>{@link bugemon.common.models.combat.strategy.MiniMaxStrategy} — AI opponent driven by alpha-beta search</li>
 * <li>{@link bugemon.common.models.combat.strategy.AutoStrategy} — random selection, used for simulations</li>
 * </ul>
 */
package bugemon.common.models.combat.strategy;
