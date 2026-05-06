package ulb.repositories.exceptions;

/** Thrown when loading a team name that does not exist for the current player. */
public class TeamNotFoundException extends Exception {
    /**
     * Constructor
     *
     * @param message
     *            the error message
     */
    public TeamNotFoundException(String message) {
        super(message);
    }
}
