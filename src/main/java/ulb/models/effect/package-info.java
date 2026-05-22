/**
 * Item and skill effects that can be applied to a {@link ulb.common.EffectTarget}.
 *
 * <p>
 * Effects follow the Visitor pattern: each concrete subclass of {@link ulb.models.effect.Effect} calls back into
 * {@link ulb.models.effect.EffectVisitor}, allowing callers to dispatch on effect type without downcasting.
 * {@link ulb.models.effect.Ticker} provides turn-based expiry tracking for temporary effects.
 */
package ulb.models.effect;
