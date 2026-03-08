/**
 * File name : ActiveEffect.java
 * Description : Wrapper for the Effect class to handle duration.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

/**
 * Represents an {@link Effect} that is currently active on a {@link Bugemon},
 * tracking the number of turns remaining before it expires.
 *
 * <p>
 * An {@code ActiveEffect} wraps an {@link Effect} and pairs it with a
 * duration counter. Each turn, {@link #decrementDuration()} should be called
 * to advance the counter. When {@link #isExpired()} returns {@code true}, the
 * effect should be removed and its stat modification reversed.
 * </p>
 *
 * <p>
 * A {@code durationLeft} value of {@code -1} indicates an infinite duration;
 * {@link #decrementDuration()} will not decrement below zero, and
 * {@link #isExpired()} will never return {@code true} for such effects.
 * </p>
 *
 * @see Effect
 * @see EffectStat
 */
public class ActiveEffect {

    /** The underlying effect being tracked. */
    private final Effect effect;

    /**
     * Number of turns remaining for this effect.
     * A value of {@code -1} means the effect lasts indefinitely.
     */
    private int durationLeft;

    /**
     * Constructs an {@code ActiveEffect} wrapping the given {@link Effect} with
     * the specified initial duration.
     *
     * @param effect       the {@link Effect} to wrap; must not be {@code null}.
     * @param durationLeft the number of turns the effect should remain active.
     *                     Pass {@code -1} for an infinite duration.
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
     * Decrements the remaining duration by one turn, if the effect has not yet
     * expired and is not infinite (i.e., {@code durationLeft > 0}).
     *
     * <p>
     * Calling this method when {@code durationLeft} is {@code 0} or {@code -1}
     * has no effect.
     * </p>
     */
    public void decrementDuration() {
        if (this.durationLeft > 0) {
            this.durationLeft--;
        }
    }

    /**
     * Returns the number of turns remaining for this effect.
     *
     * <p>
     * A return value of {@code -1} indicates the effect is infinite.
     * A return value of {@code 0} indicates the effect has expired and should
     * be removed.
     * </p>
     *
     * @return the number of turns remaining, or {@code -1} for infinite duration.
     */
    public int getDuration() {
        return this.durationLeft;
    }

    /**
     * Returns {@code true} if this effect has expired, i.e., its remaining
     * duration has reached zero.
     *
     * <p>
     * An effect with a duration of {@code -1} (infinite) will never be considered
     * expired.
     * </p>
     *
     * @return {@code true} if {@code durationLeft == 0}, {@code false} otherwise.
     */
    public boolean isExpired() {
        return this.durationLeft == 0;
    }
}
