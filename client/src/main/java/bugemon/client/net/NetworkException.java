package bugemon.client.net;

/**
 * Unchecked exception raised when a network request cannot be completed: the connection failed, dropped, timed out, or
 * the server reported an unexpected error.
 */
public class NetworkException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            description of the failure
     */
    public NetworkException(String message) {
        super(message);
    }

    /**
     * @param message
     *            description of the failure
     * @param cause
     *            the underlying cause
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
