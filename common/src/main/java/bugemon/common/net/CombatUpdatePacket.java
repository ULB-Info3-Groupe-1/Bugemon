package bugemon.common.net;

import java.util.List;

/**
 * A snapshot of combat state pushed by the server after an action has been resolved.
 *
 * <p>
 * <strong>Status:</strong> reserved for the future server-authoritative combat mode (see {@link CombatActionPacket}).
 * In the current client-side combat implementation this packet is not emitted; it documents the shape the server would
 * return so the client could re-render via {@code Platform.runLater}. The payload is deliberately string-based for now
 * so the protocol does not depend on the (not-yet-{@code Serializable}) combat model.
 *
 * @param combatId
 *            identifies the combat session this update belongs to
 * @param finished
 *            {@code true} once the combat has ended
 * @param playerWon
 *            meaningful only when {@code finished} is {@code true}
 * @param log
 *            human-readable description of the steps that just occurred, in order
 */
public record CombatUpdatePacket(String combatId, boolean finished, boolean playerWon, List<String> log)
        implements Packet {
}
