package bugemon.common.net;

/**
 * Generic acknowledgment reply for write operations that return no data.
 *
 * <p>
 * Its mere arrival signals that the server applied the request successfully; an unexpected server-side failure is
 * conveyed instead through {@link ResponseEnvelope#failed()}, which completes the client future exceptionally.
 */
public record AckPacket() implements Packet {
}
