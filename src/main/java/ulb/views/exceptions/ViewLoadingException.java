package ulb.views.exceptions;

/**
 * Thrown when a view cannot be loaded.
 */
public class ViewLoadingException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message
     *            the exception message
     * @param cause
     *            the exception cause
     */
    public ViewLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
