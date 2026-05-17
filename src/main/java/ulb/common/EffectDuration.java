package ulb.common;

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
}
