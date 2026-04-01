package ulb.common;

/**
 * Represents the effectiveness of an attack type against a defender's type.
 */
public enum Efficiency {
    /** Normal effectiveness — no bonus or penalty. */
    NEUTRAL("Neutre"),
    /** Reduced effectiveness — deals less damage. */
    LOW("Faible"),
    /** Super effectiveness — deals more damage. */
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
