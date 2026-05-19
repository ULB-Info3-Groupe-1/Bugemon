package ulb.services.exceptions;

public class TeamNameEmptyException extends Exception {
    public TeamNameEmptyException(String message) {
        super(message);
    }

    public TeamNameEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
