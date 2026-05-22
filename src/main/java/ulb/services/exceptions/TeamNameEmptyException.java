package ulb.services.exceptions;

/** Thrown when a team name is {@code null} or blank when one is required. */
public class TeamNameEmptyException extends Exception {
    public TeamNameEmptyException(String message) {
        super(message);
    }
}
