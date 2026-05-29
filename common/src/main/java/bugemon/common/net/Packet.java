package bugemon.common.net;

import java.io.Serializable;

/**
 * Marker interface for every message exchanged between the client and the authoritative server.
 *
 * <p>
 * All network messages are transferred with Java object serialization over a TCP {@link java.net.Socket}, so every
 * concrete {@code Packet} (and everything it transitively references) must be {@link Serializable}. Implementations are
 * expected to be immutable {@code record} types.
 */
public interface Packet extends Serializable {
}
