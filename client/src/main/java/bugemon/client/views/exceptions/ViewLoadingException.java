package bugemon.client.views.exceptions;

/**
 * Unchecked exception thrown by {@link bugemon.client.views.ViewLoader} when a view's FXML resource cannot be loaded or
 * its controller cannot be instantiated.
 */
public class ViewLoadingException extends RuntimeException {
    /**
     * Constructs the exception with a descriptive message and the underlying cause.
     *
     * @param message
     *            human-readable description of what failed
     * @param cause
     *            the original {@link Exception} that triggered the failure
     */
    public ViewLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
