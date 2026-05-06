package ulb.models.bugemon.components.modifier;

/** Simple countdown used to expire a {@link Modifier} after a number of ticks. */
public class Ticker {
    private int remainingTicks;

    /**
     * Constructs a ticker starting at {@code numTicks}.
     *
     * @param numTicks
     *            number of ticks before expiration
     */
    public Ticker(int numTicks) {
        this.remainingTicks = numTicks;
    }

    /** Decrements the remaining ticks down to zero. */
    public void tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    /** Returns true once the remaining ticks reach zero. */
    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }
}
