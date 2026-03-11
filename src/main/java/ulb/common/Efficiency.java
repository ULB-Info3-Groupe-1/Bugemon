package ulb.common;

/**
 * Represents the effectiveness of an attack type against a defender's type.
 */
public enum Efficiency {
    /** Normal effectiveness — no bonus or penalty. */
    NEUTRAL,
    /** Reduced effectiveness — deals less damage. */
    LOW,
    /** Super effectiveness — deals more damage. */
    HIGH,
}