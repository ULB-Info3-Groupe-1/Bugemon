package bugemon.common.net;

/**
 * Reply to a team mutation (delete / rename / modify) carrying the domain-level outcome.
 *
 * <p>
 * Domain failures the UI must react to are encoded as a {@link Status} rather than thrown as server errors, so the
 * client never sees server-side exception types.
 *
 * @param status
 *            the outcome of the requested team operation
 */
public record TeamOpResponsePacket(Status status) implements Packet {

    /** Possible outcomes of a team mutation. */
    public enum Status {
        /** The operation succeeded. */
        OK,
        /** The targeted team does not exist. */
        NOT_FOUND,
        /** The requested new name was blank. */
        NAME_EMPTY
    }
}
