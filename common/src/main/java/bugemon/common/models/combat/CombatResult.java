package bugemon.common.models.combat;

/**
 * Represents the outcome of a completed {@link Combat} from the player's perspective.
 */
public enum CombatResult {
    /** The player's team was fully defeated or the player forfeited. */
    DEFEAT,
    /** The opponent's team was fully defeated. */
    VICTORY
}
