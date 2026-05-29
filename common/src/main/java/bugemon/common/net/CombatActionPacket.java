package bugemon.common.net;

/**
 * The action a player chose on their turn, addressed to a specific combat.
 *
 * <p>
 * <strong>Status:</strong> in the current architecture combat is resolved <em>client-side</em> (the combat model lives
 * in {@code bugemon.common.models.combat} and is driven directly by the client). This packet — together with
 * {@link CombatUpdatePacket} — reserves the protocol for a future server-authoritative combat mode, in which the client
 * would only send the chosen action and render the state the server returns. It is intentionally minimal until that
 * mode is implemented.
 *
 * @param combatId
 *            identifies the combat session the action belongs to
 * @param type
 *            the kind of action chosen
 * @param targetId
 *            the attack id, target Bugemon id or item id the action refers to, or {@code null} for
 *            {@link ActionType#FORFEIT}
 */
public record CombatActionPacket(String combatId, ActionType type, String targetId) implements Packet {

    /** The categories of action a player can take during a combat turn. */
    public enum ActionType {
        /** Use one of the active Bugemon's attacks. */
        ATTACK,
        /** Switch the active Bugemon for another team member. */
        SWITCH,
        /** Use an inventory item. */
        ITEM,
        /** Give up the combat. */
        FORFEIT
    }
}
