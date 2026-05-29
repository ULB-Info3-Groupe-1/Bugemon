package bugemon.common.net;

/**
 * Authentication request sent by the client at the start of a session.
 *
 * <p>
 * The same packet covers both logging into an existing account and creating a new one, distinguished by {@link Mode}.
 *
 * @param username
 *            the player's chosen pseudonym
 * @param password
 *            the player's plaintext password (hashed and verified server-side)
 * @param mode
 *            whether this is a login attempt or an account creation
 */
public record ConnectionRequestPacket(String username, String password, Mode mode) implements Packet {

    /** Distinguishes the two authentication flows carried by a {@link ConnectionRequestPacket}. */
    public enum Mode {
        /** Authenticate against an existing account. */
        LOGIN,
        /** Register a brand-new account. */
        CREATE_ACCOUNT
    }
}
