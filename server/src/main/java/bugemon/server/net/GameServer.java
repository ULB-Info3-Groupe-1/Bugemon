package bugemon.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.server.bootstrap.GameBootstrapper;

/**
 * The authoritative TCP game server: accepts client connections and hands each one to a {@link ClientHandler} running
 * on its own virtual thread.
 *
 * <p>
 * The database must already be initialised (via {@link GameBootstrapper}) before {@link #start()} is called; the
 * dispatcher shared with every handler reuses the bootstrapper's repositories and services.
 */
public class GameServer {

    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

    private final int port;
    private final RequestDispatcher dispatcher;

    /**
     * @param port
     *            the TCP port to bind and listen on
     * @param bootstrapper
     *            the already-initialised server bootstrap providing repositories and services
     */
    public GameServer(int port, GameBootstrapper bootstrapper) {
        this.port = port;
        this.dispatcher = new RequestDispatcher(bootstrapper);
    }

    /**
     * Binds the server socket and loops forever accepting connections. Each accepted socket is served on a dedicated
     * virtual thread so that {@code accept()} is never blocked by in-flight requests.
     *
     * @throws IOException
     *             if the server socket cannot be opened or accept fails fatally
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            LOG.info("Bugemon server listening on port {}", this.port);
            while (true) {
                Socket client = serverSocket.accept();
                Thread.ofVirtual().name("bugemon-client-", 0).start(new ClientHandler(client, this.dispatcher));
            }
        }
    }
}
