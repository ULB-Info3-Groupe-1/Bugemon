package bugemon.common.net;

import java.io.Serializable;

/**
 * Wraps a client {@link Packet} together with a client-generated correlation identifier.
 *
 * <p>
 * The server echoes the same {@code correlationId} back inside the matching {@link ResponseEnvelope}, which lets the
 * client's single background reader thread match an incoming response to the request that is still waiting for it.
 *
 * @param correlationId
 *            unique, monotonically increasing id assigned by the client for this request
 * @param payload
 *            the actual request message
 */
public record RequestEnvelope(long correlationId, Packet payload) implements Serializable {
}
