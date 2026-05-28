package bugemon.common;

/**
 * Lifetime of an item or attack effect: either it expires after one combat turn, or it persists permanently for the
 * rest of the run.
 *
 * <p>
 * Each constant carries a string {@code label} that matches the value stored in the database and in the JSON resource
 * files. Use {@link #fromLabel(String)} to deserialise and {@link #toString()} to serialise.
 */
public enum EffectDuration {
    /** Effect expires at the end of the current combat turn. */
    ONE_TURN("1_tour"),
    /** Effect persists indefinitely until explicitly removed or the run ends. */
    PERMANENT("permanent");

    private final String label;

    EffectDuration(String label) {
        this.label = label;
    }

    /**
     * Returns the {@code EffectDuration} whose persistence label equals the given string.
     *
     * @param label
     *            the database/JSON label to look up (e.g. {@code "1_tour"} or {@code "permanent"})
     * @return the matching constant
     * @throws IllegalArgumentException
     *             if no constant has the given label
     */
    public static EffectDuration fromLabel(String label) {
        for (EffectDuration duration : EffectDuration.values()) {
            if (duration.label.equals(label)) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Label not found : " + label);
    }

    @Override
    public String toString() {
        return this.label;
    }
}
