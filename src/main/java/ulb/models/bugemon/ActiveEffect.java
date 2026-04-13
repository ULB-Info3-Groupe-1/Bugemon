package ulb.models.bugemon;

import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;

/**
 * An {@link Effect} that is currently active on a {@link Bugemon}, paired with a remaining-turns counter. Call
 * {@link #decrementDuration()} each turn; remove the effect when {@link #isExpired()} is true. A {@code durationLeft}
 * of {@code -1} means infinite duration.
 *
 * @see Effect
 * @see EffectStat
 */
public class ActiveEffect {
    /** The underlying effect being tracked. */
    private final Effect effect;

    /**
     * Number of turns remaining for this effect. A value of {@code -1} means the effect lasts indefinitely.
     */
    private int durationLeft;

    /**
     * Constructs an {@code ActiveEffect} wrapping the given {@link Effect} with the specified initial duration.
     *
     * @param effect
     *            the {@link Effect} to wrap; must not be {@code null}.
     * @param durationLeft
     *            the number of turns the effect should remain active. Pass {@code -1} for an infinite duration.
     */
    public ActiveEffect(Effect effect, int durationLeft) {
        this.effect = effect;
        this.durationLeft = durationLeft;
    }

    /**
     * Returns the underlying {@link Effect} wrapped by this active effect.
     *
     * @return the {@link Effect} associated with this active effect.
     */
    public Effect getEffect() {
        return this.effect;
    }

    /**
     * Decrements the remaining duration by one turn. No-op when {@code durationLeft} is {@code 0} (expired) or
     * {@code -1} (infinite).
     */
    public void decrementDuration() {
        if (this.durationLeft > 0) {
            this.durationLeft--;
        }
    }

    /**
     * Returns the turns remaining, {@code -1} for infinite duration, {@code 0} if expired and should be removed.
     */
    public int getDuration() {
        return this.durationLeft;
    }

    /** Returns {@code true} when {@code durationLeft == 0}; always {@code false} for infinite effects. */
    public boolean isExpired() {
        return this.durationLeft == 0;
    }
}
