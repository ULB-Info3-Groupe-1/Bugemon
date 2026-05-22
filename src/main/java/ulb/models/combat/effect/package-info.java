/**
 * In-combat status effects that modify a Bugemon's stats for a limited duration.
 *
 * <p>
 * {@link ulb.models.combat.effect.StatusEffect} is the sole class in this package. It encapsulates a signed stat
 * modifier, the targeted {@link ulb.common.StatType}, and an optional lifespan tracked by a
 * {@link ulb.models.effect.Ticker}. Permanent effects ({@link ulb.common.EffectDuration#PERMANENT}) have no ticker and
 * never expire.
 */
package ulb.models.combat.effect;
