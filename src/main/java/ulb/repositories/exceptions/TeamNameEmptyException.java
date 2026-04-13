package ulb.repositories.exceptions;

/** Thrown when a team name is blank or null. */
public class TeamNameEmptyException extends Exception {
    public TeamNameEmptyException(String message) {
        super(message);
    }
}
