package ulb.models.bugemon;

/**
 * Represents the effectiveness of an attack type against a defender's type.
 */
public enum Efficiency {
    NEUTRAL("Neutre"),
    LOW("Faible"),
    HIGH("Élevée");

    private final String label;

    Efficiency(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
