/**
 * Network protocol shared by the client and the authoritative server.
 *
 * <p>
 * Every message is a {@link bugemon.common.net.Packet} (an immutable {@code Serializable} record) exchanged over a TCP
 * socket using Java object serialization. Requests and responses are wrapped in
 * {@link bugemon.common.net.RequestEnvelope} and {@link bugemon.common.net.ResponseEnvelope} so that a single
 * background reader thread on the client can correlate each reply with the request still awaiting it.
 */
package bugemon.common.net;
