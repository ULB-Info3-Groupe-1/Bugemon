package bugemon.common.net;

/**
 * Reply to a {@link CreateBugemonRequestPacket} carrying the domain-level outcome of the creation attempt.
 *
 * @param status
 *            the outcome of the creation
 */
public record CreateBugemonResponsePacket(Status status) implements Packet {

    /** Possible outcomes of a custom-Bugemon creation. */
    public enum Status {
        /** The Bugemon was created and persisted. */
        OK,
        /** The provided name was blank. */
        NAME_EMPTY,
        /** A Bugemon with that name already exists. */
        NAME_EXISTS
    }
}
