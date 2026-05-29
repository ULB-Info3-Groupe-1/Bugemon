package bugemon.client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.Configuration;
import bugemon.common.net.Packet;
import bugemon.common.net.RequestEnvelope;
import bugemon.common.net.ResponseEnvelope;

/**
 * Client-side singleton that owns the TCP connection to the authoritative server.
 *
 * <p>
 * It exposes an asynchronous request/response API: {@link #sendAsync(Packet, Class)} returns a
 * {@link CompletableFuture} that completes (off the JavaFX thread) when the server replies. A single background
 * <em>listener</em> thread reads every {@link ResponseEnvelope} and completes the matching future by correlation id,
 * which lets many requests be in flight at once. Callers are expected to update the UI from the future's continuation
 * via {@code Platform.runLater}, never on this class's threads.
 *
 * <p>
 * Connection establishment is lazy and happens on a background virtual thread, so calling {@link #sendAsync} from the
 * JavaFX application thread never blocks it.
 */
public final class NetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);
    private static final NetworkManager INSTANCE = new NetworkManager();

    private final AtomicLong nextCorrelationId = new AtomicLong(1);
    private final ConcurrentHashMap<Long, CompletableFuture<ResponseEnvelope>> pending = new ConcurrentHashMap<>();

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean connected;

    private NetworkManager() {
        // Singleton.
    }

    /** @return the shared {@code NetworkManager} instance. */
    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    /**
     * Opens the connection to the default server host/port if it is not already open.
     *
     * @throws IOException
     *             if the socket cannot be opened
     */
    public synchronized void connect() throws IOException {
        this.connect(Configuration.Net.HOST, Configuration.Net.PORT);
    }

    /**
     * Opens the connection to the given server endpoint if it is not already open, then starts the background listener
     * thread.
     *
     * @param host
     *            server hostname
     * @param port
     *            server TCP port
     * @throws IOException
     *             if the socket cannot be opened
     */
    public synchronized void connect(String host, int port) throws IOException {
        if (this.connected) {
            return;
        }
        this.socket = new Socket(host, port);
        // Create and flush the output stream header before the input stream to match the server's stream ordering.
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.connected = true;
        Thread.ofVirtual().name("network-listener").start(this::listen);
        LOG.info("Connected to server {}:{}", host, port);
    }

    /**
     * Sends a request and returns a future that completes with the typed reply.
     *
     * @param request
     *            the request packet
     * @param responseType
     *            the expected reply packet class
     * @param <T>
     *            the reply packet type
     * @return a future completing with the reply, or completing exceptionally with a {@link NetworkException}
     */
    public <T extends Packet> CompletableFuture<T> sendAsync(Packet request, Class<T> responseType) {
        return this.sendAsync(request).thenApply(responseType::cast);
    }

    /**
     * Sends a request and returns a future that completes with the untyped reply.
     *
     * @param request
     *            the request packet
     * @return a future completing with the reply, or completing exceptionally with a {@link NetworkException}
     */
    public CompletableFuture<Packet> sendAsync(Packet request) {
        long id = this.nextCorrelationId.getAndIncrement();
        CompletableFuture<ResponseEnvelope> future = new CompletableFuture<>();
        this.pending.put(id, future);
        Thread.ofVirtual().name("network-send-" + id).start(() -> this.dispatchWrite(id, request, future));
        return future.thenApply(this::unwrap);
    }

    /**
     * Sends a request and blocks the calling thread until the reply arrives. Must never be called on the JavaFX
     * application thread.
     *
     * @param request
     *            the request packet
     * @param responseType
     *            the expected reply packet class
     * @param <T>
     *            the reply packet type
     * @return the typed reply
     */
    public <T extends Packet> T send(Packet request, Class<T> responseType) {
        try {
            Packet reply = this.sendAsync(request).get(Configuration.Net.REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            return responseType.cast(reply);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while awaiting server reply", e);
        } catch (ExecutionException e) {
            throw new NetworkException("Request failed", e.getCause());
        } catch (TimeoutException e) {
            throw new NetworkException("Server did not reply within the timeout", e);
        }
    }

    /** Closes the connection and fails any in-flight requests. */
    public synchronized void disconnect() {
        this.connected = false;
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            LOG.warn("Error while closing socket: {}", e.getMessage());
        }
        this.failAllPending(new NetworkException("Disconnected"));
    }

    private void dispatchWrite(long id, Packet request, CompletableFuture<ResponseEnvelope> future) {
        try {
            this.ensureConnected();
            this.writeRequest(new RequestEnvelope(id, request));
        } catch (IOException e) {
            this.pending.remove(id);
            future.completeExceptionally(new NetworkException("Failed to send request", e));
        }
    }

    private synchronized void ensureConnected() throws IOException {
        if (!this.connected) {
            this.connect();
        }
    }

    private synchronized void writeRequest(RequestEnvelope envelope) throws IOException {
        this.out.writeObject(envelope);
        this.out.flush();
        // Forget cached references so each request serialises a fresh object graph.
        this.out.reset();
    }

    private Packet unwrap(ResponseEnvelope envelope) {
        if (envelope.failed()) {
            throw new NetworkException("Server error: " + envelope.error());
        }
        return envelope.payload();
    }

    private void listen() {
        try {
            while (this.connected) {
                ResponseEnvelope envelope = (ResponseEnvelope) this.in.readObject();
                CompletableFuture<ResponseEnvelope> future = this.pending.remove(envelope.correlationId());
                if (future != null) {
                    future.complete(envelope);
                } else {
                    LOG.warn("Received reply for unknown correlation id {}", envelope.correlationId());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (this.connected) {
                LOG.warn("Connection to server lost: {}", e.getMessage());
            }
            this.failAllPending(new NetworkException("Connection lost", e));
        }
    }

    private void failAllPending(NetworkException cause) {
        this.connected = false;
        this.pending.forEach((id, future) -> future.completeExceptionally(cause));
        this.pending.clear();
    }
}
