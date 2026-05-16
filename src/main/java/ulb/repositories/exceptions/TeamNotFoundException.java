package ulb.repositories.exceptions;

/** Thrown when loading a team name that does not exist for the current player. */
public class TeamNotFoundException extends Exception {
    public TeamNotFoundException(String message) {
        super(message);
    }

    public TeamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
