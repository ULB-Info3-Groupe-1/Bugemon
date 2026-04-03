package ulb.services.exceptions;

/** Thrown when loading a team name that does not exist for the current user. */
public class TeamNotFoundException extends Exception {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
