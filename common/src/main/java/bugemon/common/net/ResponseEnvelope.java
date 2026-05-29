package bugemon.common.net;

import java.io.Serializable;

/**
 * Wraps a server {@link Packet} reply together with the correlation id of the request it answers.
 *
 * <p>
 * Domain-level outcomes (e.g. "invalid credentials") are encoded inside the {@code payload} itself. The {@code error}
 * field is reserved for unexpected server-side failures (an uncaught exception while handling the request); when it is
 * non-{@code null} the {@code payload} is {@code null} and the client should surface a generic error.
 *
 * @param correlationId
 *            id copied from the {@link RequestEnvelope} this response answers
 * @param payload
 *            the reply message, or {@code null} when {@code error} is set
 * @param error
 *            a human-readable description of an unexpected server failure, or {@code null} on success
 */
public record ResponseEnvelope(long correlationId, Packet payload, String error) implements Serializable {

    /**
     * Builds a successful response carrying the given payload.
     *
     * @param correlationId
     *            id of the request being answered
     * @param payload
     *            the reply message
     * @return a response envelope with no error
     */
    public static ResponseEnvelope ok(long correlationId, Packet payload) {
        return new ResponseEnvelope(correlationId, payload, null);
    }

    /**
     * Builds a failure response describing an unexpected server error.
     *
     * @param correlationId
     *            id of the request being answered
     * @param error
     *            description of the failure
     * @return a response envelope with no payload
     */
    public static ResponseEnvelope failure(long correlationId, String error) {
        return new ResponseEnvelope(correlationId, null, error);
    }

    /** @return {@code true} if this response represents an unexpected server failure. */
    public boolean failed() {
        return this.error != null;
    }
}
