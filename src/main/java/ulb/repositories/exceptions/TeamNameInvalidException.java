package ulb.repositories.exceptions;

public class TeamNameInvalidException extends Exception {
    public TeamNameInvalidException(String message) {
        super(message);
    }

    public TeamNameInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
