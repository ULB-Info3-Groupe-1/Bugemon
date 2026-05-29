package bugemon.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.Configuration;
import bugemon.server.bootstrap.GameBootstrapper;
import bugemon.server.net.GameServer;

/**
 * Standalone entry point for the Bugemon server.
 *
 * <p>
 * Bootstraps the database (schema creation + static data seeding through {@link GameBootstrapper}, which opens the
 * {@link bugemon.server.repositories.DatabaseConnection}), then starts the authoritative {@link GameServer} that serves
 * clients over TCP using virtual threads.
 */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {
        // Entry point only; not instantiable.
    }

    /**
     * Initialises the database and runs the game server until the process is terminated.
     *
     * @param args
     *            unused command-line arguments
     */
    public static void main(String[] args) {
        LOG.info("Starting Bugemon server...");
        GameBootstrapper bootstrapper = new GameBootstrapper();
        LOG.info("Database initialised. Starting socket server on port {}.", Configuration.Net.PORT);

        GameServer server = new GameServer(Configuration.Net.PORT, bootstrapper);
        try {
            server.start();
        } catch (IOException e) {
            LOG.error("Server stopped: failed to listen on port {}", Configuration.Net.PORT, e);
        }
    }
}
