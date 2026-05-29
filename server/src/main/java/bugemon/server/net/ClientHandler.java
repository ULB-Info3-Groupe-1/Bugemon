package bugemon.server.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.net.Packet;
import bugemon.common.net.RequestEnvelope;
import bugemon.common.net.ResponseEnvelope;

/**
 * Serves a single client connection: reads {@link RequestEnvelope}s, dispatches them, and writes back the matching
 * {@link ResponseEnvelope}.
 *
 * <p>
 * One instance runs per accepted socket on its own <em>virtual thread</em>, so the blocking {@code readObject} call
 * costs almost nothing while idle and the server scales to many simultaneous connections without a thread pool.
 */
public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket socket;
    private final RequestDispatcher dispatcher;

    /**
     * @param socket
     *            the freshly accepted client socket this handler owns and will close
     * @param dispatcher
     *            the shared dispatcher used to produce replies
     */
    public ClientHandler(Socket socket, RequestDispatcher dispatcher) {
        this.socket = socket;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        String remote = String.valueOf(this.socket.getRemoteSocketAddress());
        LOG.info("Client connected: {}", remote);
        ClientSession session = new ClientSession();

        // The ObjectOutputStream must be created (and its header flushed) before the ObjectInputStream to avoid a
        // handshake deadlock with the client, which orders its streams the same way.
        try (Socket client = this.socket;
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
            this.serve(in, out, session);
        } catch (EOFException e) {
            LOG.info("Client {} disconnected", remote);
        } catch (IOException e) {
            LOG.warn("Client {} connection error: {}", remote, e.getMessage());
        }
        LOG.info("Client session ended: {}", remote);
    }

    private void serve(ObjectInputStream in, ObjectOutputStream out, ClientSession session) throws IOException {
        while (true) {
            RequestEnvelope envelope = this.readEnvelope(in);
            ResponseEnvelope response = this.handle(envelope, session);
            out.writeObject(response);
            out.flush();
            // Drop references cached by the stream so repeated objects are re-serialised fresh and not leaked.
            out.reset();
        }
    }

    private RequestEnvelope readEnvelope(ObjectInputStream in) throws IOException {
        try {
            return (RequestEnvelope) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Received an unknown packet type", e);
        }
    }

    private ResponseEnvelope handle(RequestEnvelope envelope, ClientSession session) {
        try {
            Packet reply = this.dispatcher.dispatch(envelope.payload(), session);
            return ResponseEnvelope.ok(envelope.correlationId(), reply);
        } catch (RuntimeException e) {
            LOG.error("Failed to handle {}", envelope.payload(), e);
            return ResponseEnvelope.failure(envelope.correlationId(), e.getMessage());
        }
    }
}
