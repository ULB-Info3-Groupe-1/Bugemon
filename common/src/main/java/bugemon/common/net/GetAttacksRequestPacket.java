package bugemon.common.net;

/**
 * Request asking the server for the full list of static attack definitions.
 *
 * <p>
 * Attacks are player-independent static data; the client caches them and filters by element type locally. The reply is
 * an {@link AttacksResponsePacket}.
 */
public record GetAttacksRequestPacket() implements Packet {
}
