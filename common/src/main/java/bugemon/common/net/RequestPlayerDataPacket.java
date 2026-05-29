package bugemon.common.net;

/**
 * Request asking the server to send the authenticated player's initial state snapshot.
 *
 * <p>
 * The request carries no fields: the server identifies the player from the already-authenticated connection session.
 * The reply is a {@link PlayerSnapshotPacket}.
 */
public record RequestPlayerDataPacket() implements Packet {
}
