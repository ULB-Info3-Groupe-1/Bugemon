package ulb.models.combat.effect;

import ulb.common.EffectDuration;
import ulb.common.StatType;
import ulb.models.effect.Ticker;

/**
 * Represents a temporary or permanent modifier applied to one stat of a {@link ulb.models.combat.CombatBugemon} during
 * combat.
 *
 * <p>
 * A {@code StatusEffect} targets a single {@link ulb.common.StatType} and adjusts it by an integer {@code modifier}
 * (positive for a bonus, negative for a malus). Effects with {@link ulb.common.EffectDuration#PERMANENT} never expire;
 * all other effects use an internal {@link ulb.models.effect.Ticker} and expire after one tick (one turn).
 *
 * <p>
 * At end-of-turn, {@link ulb.models.combat.CombatBugemon#tickEffects()} advances each effect and discards expired ones.
 */
public class StatusEffect {
    private final StatType stat;
    private final int modifier;
    private Ticker ticker; // NOTE: A permanent effect is represented by a null ticker

    /**
     * Constructs a {@code StatusEffect} targeting the given stat.
     *
     * @param stat
     *            the stat to modify
     * @param modifier
     *            the signed amount to add to the stat (negative for a malus)
     * @param duration
     *            {@link ulb.common.EffectDuration#PERMANENT} for a lasting effect, or any other value for a one-turn
     *            effect
     */
    public StatusEffect(StatType stat, int modifier, EffectDuration duration) {
        this.stat = stat;
        this.modifier = modifier;
        this.ticker = (duration == EffectDuration.PERMANENT) ? null : new Ticker(1);
    }

    public StatType getStat() {
        return this.stat;
    }

    public int getModifier() {
        return this.modifier;
    }

    public boolean isExpired() {
        return this.ticker != null && this.ticker.isExpired();
    }

    /**
     * Advances the internal ticker by one step. Has no effect on permanent effects.
     */
    public void tick() {
        if (this.ticker != null) {
            this.ticker.tick();
        }
    }

    public boolean isNegative() {
        return this.modifier < 0;
    }
}
