package bugemon.common.models.combat.effect;

import bugemon.common.EffectDuration;
import bugemon.common.StatType;
import bugemon.common.models.effect.Ticker;

/**
 * Represents a temporary or permanent modifier applied to one stat of a
 * {@link bugemon.common.models.combat.CombatBugemon} during combat.
 *
 * <p>
 * A {@code StatusEffect} targets a single {@link bugemon.common.StatType} and adjusts it by an integer {@code modifier}
 * (positive for a bonus, negative for a malus). Effects with {@link bugemon.common.EffectDuration#PERMANENT} never
 * expire; all other effects use an internal {@link bugemon.common.models.effect.Ticker} and expire after one tick (one
 * turn).
 *
 * <p>
 * At end-of-turn, {@link bugemon.common.models.combat.CombatBugemon#tickEffects()} advances each effect and discards
 * expired ones.
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
     *            {@link bugemon.common.EffectDuration#PERMANENT} for a lasting effect, or any other value for a
     *            one-turn effect
     */
    public StatusEffect(StatType stat, int modifier, EffectDuration duration) {
        this.stat = stat;
        this.modifier = modifier;
        this.ticker = (duration == EffectDuration.PERMANENT) ? null : new Ticker(1);
    }

    /**
     * Returns the stat that this effect modifies.
     *
     * @return the targeted {@link StatType}
     */
    public StatType getStat() {
        return this.stat;
    }

    /**
     * Returns the signed amount by which this effect modifies its target stat. Positive values are bonuses; negative
     * values are maluses.
     *
     * @return the stat modifier
     */
    public int getModifier() {
        return this.modifier;
    }

    /**
     * Returns {@code true} if this effect's lifespan has elapsed and it should be removed. Permanent effects (no
     * ticker) never expire.
     *
     * @return {@code true} when the effect has run its course
     */
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

    /**
     * Returns {@code true} if this effect applies a penalty (negative modifier) to its target stat.
     *
     * @return {@code true} when the modifier is less than zero
     */
    public boolean isNegative() {
        return this.modifier < 0;
    }
}
