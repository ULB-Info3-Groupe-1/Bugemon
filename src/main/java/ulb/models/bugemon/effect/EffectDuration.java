package ulb.models.bugemon.effect;

/** Duration of an attack effect: either consumed after one combat turn or permanent until reset. */
public enum EffectDuration {
    ONE_TURN("1_tour"),
    PERMANENT("permanent");

    private final String label;

    EffectDuration(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    /**
     * Returns the {@code EffectDuration} whose serialised label equals {@code label}.
     *
     * @throws IllegalArgumentException
     *             if no value matches the label
     */
    public static EffectDuration fromLabel(String label) {
        for (EffectDuration duration : EffectDuration.values()) {
            if (duration.label.equals(label)) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Label not found : " + label);
    }
}
