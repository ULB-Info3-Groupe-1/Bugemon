package ulb.models.bugemon;

/**
 * Represents the elemental type of a bugemon.
 * <p>
 * Each type may have strengths and weaknesses against other types during
 * combat.
 * </p>
 *
 * <ul>
 *   <li>{@link #FLORA}  – plant/nature-based type.</li>
 *   <li>{@link #AQUA}   – water-based type.</li>
 *   <li>{@link #PYRO}   – fire-based type.</li>
 *   <li>{@link #LITHO}  – rock/earth-based type.</li>
 * </ul>
 */
public enum BugemonType {
    /** Plant/nature-based elemental type. */
    FLORA,
    /** Water-based elemental type. */
    AQUA,
    /** Fire-based elemental type. */
    PYRO,
    /** Rock/earth-based elemental type. */
    LITHO,
}