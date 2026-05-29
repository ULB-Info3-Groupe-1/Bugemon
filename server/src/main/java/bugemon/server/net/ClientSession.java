package bugemon.server.net;

import bugemon.server.bootstrap.ServiceRegistry;

/**
 * Mutable per-connection state held by the {@link ClientHandler} for the lifetime of a single client socket.
 *
 * <p>
 * Once the client authenticates, the dispatcher builds the player's session-bound {@link ServiceRegistry} (the
 * server-side equivalent of what the client used to assemble in-process after login) and stores it here so subsequent
 * requests on the same connection reuse it.
 */
public class ClientSession {

    private String playerName;
    private ServiceRegistry services;

    /**
     * Marks this session as authenticated for the given player and binds its services.
     *
     * @param name
     *            the authenticated pseudonym
     * @param sessionServices
     *            the player's session-bound services
     */
    public void authenticate(String name, ServiceRegistry sessionServices) {
        this.playerName = name;
        this.services = sessionServices;
    }

    /** @return {@code true} once {@link #authenticate} has been called. */
    public boolean isAuthenticated() {
        return this.playerName != null;
    }

    /** @return the authenticated pseudonym, or {@code null} if not yet authenticated. */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Returns the player's session-bound services.
     *
     * @return the bound {@link ServiceRegistry}
     * @throws IllegalStateException
     *             if the session is not yet authenticated
     */
    public ServiceRegistry getServices() {
        if (this.services == null) {
            throw new IllegalStateException("Session is not authenticated");
        }
        return this.services;
    }
}
