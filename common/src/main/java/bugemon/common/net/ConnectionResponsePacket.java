package bugemon.common.net;

/**
 * Server reply to a {@link ConnectionRequestPacket}.
 *
 * <p>
 * The {@link Status} enumerates every domain-level outcome the client must react to, so the client never needs to know
 * about server-side persistence exceptions. On success, {@code playerName} echoes the authenticated pseudonym the rest
 * of the client session is keyed on.
 *
 * @param success
 *            {@code true} when the player is now authenticated
 * @param status
 *            the precise outcome of the attempt
 * @param playerName
 *            the authenticated pseudonym on success, otherwise {@code null}
 */
public record ConnectionResponsePacket(boolean success, Status status, String playerName) implements Packet {

    /** Every outcome of an authentication attempt the client UI can present. */
    public enum Status {
        /** Authentication (or account creation) succeeded. */
        OK,
        /** The password did not match the stored credentials. */
        INVALID_CREDENTIALS,
        /** No account exists for the supplied pseudonym. */
        IDENTIFIER_NOT_FOUND,
        /** Account creation failed because the pseudonym is already taken. */
        USERNAME_ALREADY_EXISTS,
        /** An unexpected server-side error occurred while processing the request. */
        SERVER_ERROR
    }

    /**
     * Builds a successful authentication response for the given player.
     *
     * @param playerName
     *            the authenticated pseudonym
     * @return a success response
     */
    public static ConnectionResponsePacket success(String playerName) {
        return new ConnectionResponsePacket(true, Status.OK, playerName);
    }

    /**
     * Builds a failed authentication response carrying the reason.
     *
     * @param status
     *            the failure reason (must not be {@link Status#OK})
     * @return a failure response with no player name
     */
    public static ConnectionResponsePacket failure(Status status) {
        return new ConnectionResponsePacket(false, status, null);
    }
}
