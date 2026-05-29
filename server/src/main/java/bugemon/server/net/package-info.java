/**
 * Server-side TCP networking: the {@link bugemon.server.net.GameServer} accept loop, per-connection
 * {@link bugemon.server.net.ClientHandler}s running on virtual threads, the shared
 * {@link bugemon.server.net.RequestDispatcher}, and the per-connection {@link bugemon.server.net.ClientSession}.
 */
package bugemon.server.net;
