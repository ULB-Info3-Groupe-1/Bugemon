package bugemon.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.server.bootstrap.GameBootstrapper;

/**
 * Standalone entry point for the Bugemon server.
 *
 * <p>
 * For now this bootstraps the database (schema creation + static data seeding).
 * Socket networking will be wired here once the multiplayer layer is implemented.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("Starting Bugemon server...");
        GameBootstrapper bootstrapper = new GameBootstrapper();
        LOG.info("Database initialised. Server ready.");
        // TODO: démarrer le socket serveur ici
    }
}
