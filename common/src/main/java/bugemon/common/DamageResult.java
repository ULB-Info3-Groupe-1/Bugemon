package bugemon.common;

import bugemon.common.models.combat.damage.Efficiency;

/**
 * Immutable result of a single damage calculation, bundling the raw damage value with the type-matchup efficiency.
 *
 * @param damage
 *            the amount of HP to subtract from the defending Bugemon
 * @param efficiency
 *            the elemental type-matchup outcome that determined any damage multiplier
 */
public record DamageResult(int damage, Efficiency efficiency) {
}
