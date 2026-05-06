package ulb.models.bugemon.components.modifier;

/**
 * A Ticker counts down the number of remaining turns for a modifier.
 */
public class Ticker {
    private int remainingTicks;

    /**
     * Creates a new Ticker with the given number of ticks.
     *
     * @param numTicks
     *            the number of ticks
     */
    public Ticker(int numTicks) {
        this.remainingTicks = numTicks;
    }

    /**
     * Decrements the number of remaining ticks.
     */
    public void tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    /**
     * Checks if the ticker has expired.
     *
     * @return true if the ticker has expired
     */
    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }
}
