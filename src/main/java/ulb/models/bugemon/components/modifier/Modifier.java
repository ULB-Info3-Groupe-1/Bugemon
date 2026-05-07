package ulb.models.bugemon.components.modifier;

import java.util.Optional;

/**
 * Additive stat modifier, optionally expiring after a number of ticks.
 * <p>
 * A modifier applies as {@code value + amount}.
 */
public class Modifier {
    Optional<Ticker> ticker;
    private int amount;

    /**
     * Constructs a permanent modifier.
     *
     * @param amount
     *            additive delta to apply
     */
    public Modifier(int amount) {
        this.ticker = Optional.empty();
        this.amount = amount;
    }

    /**
     * Constructs a temporary modifier.
     *
     * @param amount
     *            additive delta to apply
     * @param numTicks
     *            number of ticks before expiration
     */
    public Modifier(int amount, int numTicks) {
        this.ticker = Optional.of(new Ticker(numTicks));
        this.amount = amount;
    }

    /** Advances the internal ticker by one, if any. */
    public void tick() {
        this.ticker.ifPresent(Ticker::tick);
    }

    /** Returns true if this modifier has an expired ticker. */
    public boolean isExpired() {
        return this.ticker.map(Ticker::isExpired).orElse(false);
    }

    /**
     * Returns true if the modifier is a malus.
     *
     * WARN: a modifier with a negative amount is considered as a malus. This lacks flexiblity and might need to change
     * in the future, but should work for now because the higher the initiative/defense/attack the better. Consequently,
     * a negative modifier must be a malus.
     */
    public boolean isMalus() {
        return this.amount < 0;
    }

    /**
     * Applies this modifier to the given value.
     *
     * @throws IllegalStateException
     *             if called after the modifier expired
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
