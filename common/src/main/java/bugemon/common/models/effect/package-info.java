/**
 * Item and skill effects that can be applied to a {@link bugemon.common.EffectTarget}.
 *
 * <p>
 * Effects follow the Visitor pattern: each concrete subclass of {@link bugemon.common.models.effect.Effect} calls back into
 * {@link bugemon.common.models.effect.EffectVisitor}, allowing callers to dispatch on effect type without downcasting.
 * {@link bugemon.common.models.effect.Ticker} provides turn-based expiry tracking for temporary effects.
 */
package bugemon.common.models.effect;
