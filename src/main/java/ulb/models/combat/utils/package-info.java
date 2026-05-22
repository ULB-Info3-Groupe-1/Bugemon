/**
 * Provides stateless utility classes shared across the combat subsystem.
 *
 * <p>
 * {@link ulb.models.combat.utils.CombatContext} bundles the two opposing {@link ulb.models.combat.CombatTeam} instances
 * and their respective {@link ulb.models.item.Inventory} objects into a single value object that is passed through the
 * resolution pipeline.
 *
 * <p>
 * {@link ulb.models.combat.utils.EffectProcessor} applies {@link ulb.models.effect.Effect} instances from attacks to
 * the correct target (thrower, opponent, or whole team) and returns the resulting
 * {@link ulb.models.combat.turn.TurnStep} events.
 */
package ulb.models.combat.utils;
