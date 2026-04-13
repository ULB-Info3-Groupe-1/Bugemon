package ulb.models.bugemon.components.modifier;

/**
 * Counts down a fixed number of ticks; expires (returns {@code true} from {@link #isExpired()}) when the counter
 * reaches zero.
 */
public class Ticker {
    private int remainingTicks;

    public Ticker(int numTicks) {
        this.remainingTicks = numTicks;
    }

    public void tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }
}
