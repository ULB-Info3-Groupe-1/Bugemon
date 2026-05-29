/**
 * Client-side TCP networking. The {@link bugemon.client.net.NetworkManager} singleton owns the socket to the
 * authoritative server and exposes an asynchronous request/response API; controllers send packets and update the JavaFX
 * UI from the returned future via {@code Platform.runLater}.
 */
package bugemon.client.net;
