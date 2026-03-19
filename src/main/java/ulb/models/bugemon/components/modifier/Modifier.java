package ulb.models.bugemon.components.modifier;

import java.util.Optional;

public abstract class Modifier {
    Optional<Ticker> ticker;
    private int amount;

    public Modifier(int amount) {
        this.ticker = Optional.empty();
        this.amount = amount;
    }

    public Modifier(int amount, int numTicks) {
        this.ticker = Optional.of(new Ticker(numTicks));
        this.amount = amount;
    }

    public void tick() {
        this.ticker.ifPresent(Ticker::tick);
    }

    public boolean isExpired() {
        return this.ticker.map(Ticker::isExpired).orElse(false);
    }

    public int apply(int value) {
        return value + this.amount;
    }
}
