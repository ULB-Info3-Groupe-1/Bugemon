/**
 * In-combat status effects that modify a Bugemon's stats for a limited duration.
 *
 * <p>
 * {@link bugemon.common.models.combat.effect.StatusEffect} is the sole class in this package. It encapsulates a signed
 * stat modifier, the targeted {@link bugemon.common.StatType}, and an optional lifespan tracked by a
 * {@link bugemon.common.models.effect.Ticker}. Permanent effects ({@link bugemon.common.EffectDuration#PERMANENT}) have
 * no ticker and never expire.
 */
package bugemon.common.models.combat.effect;
