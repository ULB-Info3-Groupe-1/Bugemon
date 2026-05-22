package ulb.models.effect;

/**
 * Counts down a fixed number of ticks and signals expiry when the count reaches zero.
 *
 * <p>
 * Used to track the remaining duration of temporary effects across combat turns.
 */
public class Ticker {
    private int remainingTicks;

    /**
     * Constructs a ticker starting at {@code numTicks}.
     *
     * @param numTicks
     *            number of ticks before expiration
     */
    public Ticker(int numTicks) {
        this.remainingTicks = Math.max(0, numTicks);
    }

    /** Decrements the remaining ticks down to zero. */
    public void tick() {
        this.remainingTicks = Math.max(0, this.remainingTicks - 1);
    }

    /** Returns true once the remaining ticks reach zero. */
    public boolean isExpired() {
        return this.remainingTicks == 0;
    }
}
