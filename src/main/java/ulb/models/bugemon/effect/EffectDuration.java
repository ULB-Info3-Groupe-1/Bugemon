package ulb.models.bugemon.effect;

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
     * Get the EffectDuration enum value corresponding to the given label.
     * @param label the label to search for
     * @return the EffectDuration enum value corresponding to the given label
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
