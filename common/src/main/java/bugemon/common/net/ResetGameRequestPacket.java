package bugemon.common.net;

/**
 * Request asking the server to wipe the authenticated player's saved progression and start a fresh game.
 *
 * <p>
 * The request carries no fields: the server identifies the player from the authenticated connection session. The reply
 * is a {@link PlayerSnapshotPacket} describing the freshly-reset state (no active team, default inventory, empty skill
 * tree) so the client can rebuild its session exactly as it does at login.
 */
public record ResetGameRequestPacket() implements Packet {
}
