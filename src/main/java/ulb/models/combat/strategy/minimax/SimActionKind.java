package ulb.models.combat.strategy.minimax;

/**
 * Enumerates the categories of action a combatant can take during a simulated turn.
 *
 * <ul>
 * <li>{@link #ATTACK} — use one of the active Bugemon's attacks</li>
 * <li>{@link #SWITCH} — swap the active Bugemon for another team member</li>
 * <li>{@link #ITEM} — use an item from the trainer's inventory</li>
 * <li>{@link #NONE} — no action (sentinel for absent half-turn actions in simultaneous resolution)</li>
 * </ul>
 */
public enum SimActionKind {
    ATTACK,
    SWITCH,
    ITEM,
    NONE
}
