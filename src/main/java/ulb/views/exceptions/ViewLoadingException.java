package ulb.views.exceptions;

public class ViewLoadingException extends RuntimeException {
    public ViewLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
