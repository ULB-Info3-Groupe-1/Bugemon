/**
 * Provides stateless utility classes shared across the combat subsystem.
 *
 * <p>
 * {@link bugemon.common.models.combat.utils.CombatContext} bundles the two opposing
 * {@link bugemon.common.models.combat.CombatTeam} instances and their respective
 * {@link bugemon.common.models.item.Inventory} objects into a single value object that is passed through the resolution
 * pipeline.
 *
 * <p>
 * {@link bugemon.common.models.combat.utils.EffectProcessor} applies {@link bugemon.common.models.effect.Effect}
 * instances from attacks to the correct target (thrower, opponent, or whole team) and returns the resulting
 * {@link bugemon.common.models.combat.turn.TurnStep} events.
 */
package bugemon.common.models.combat.utils;
