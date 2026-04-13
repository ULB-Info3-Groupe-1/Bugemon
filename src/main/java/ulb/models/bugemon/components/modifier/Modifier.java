package ulb.models.bugemon.components.modifier;

import java.util.Optional;

/**
 * An integer delta applied to a stat value. Carries an optional {@link Ticker} that counts down remaining turns;
 * modifiers without a ticker are permanent and never expire.
 */
public class Modifier {
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

    /**
     * Returns {@code true} if this modifier reduces a stat (negative amount).
     *
     * Assumes higher is always better for all stats; a negative delta is therefore always a malus.
     */
    public boolean isMalus() {
        return this.amount < 0;
    }

    /**
     * Applies the modifier delta to {@code value} and returns the result.
     *
     * @throws IllegalStateException
     *             if the modifier has a ticker that is already expired
     */
    public int apply(int value) {
        this.ticker.ifPresent(t -> {
            if (t.isExpired()) {
                throw new IllegalStateException("apply called on an expired modifier");
            }
        });

        return value + this.amount;
    }
}
