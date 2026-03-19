package ulb.models.bugemon.components.modifier;

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
