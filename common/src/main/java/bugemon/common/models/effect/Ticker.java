package bugemon.common.models.effect;

/**
 * Counts down a fixed number of ticks and signals expiry when the count reaches zero.
 *
 * <p>
 * Used to track the remaining duration of temporary effects across combat turns. Each call to {@link #tick()}
 * represents one elapsed turn; {@link #isExpired()} returns {@code true} once the counter reaches zero.
 */
public class Ticker {
    private int remainingTicks;

    /**
     * Constructs a ticker starting at {@code numTicks}.
     *
     * @param numTicks
     *            number of ticks before expiration; clamped to zero if negative
     */
    public Ticker(int numTicks) {
        this.remainingTicks = Math.max(0, numTicks);
    }

    /** Decrements the remaining tick count by one, down to a minimum of zero. */
    public void tick() {
        this.remainingTicks = Math.max(0, this.remainingTicks - 1);
    }

    /** Returns {@code true} once the remaining ticks reach zero. */
    public boolean isExpired() {
        return this.remainingTicks == 0;
    }
}
