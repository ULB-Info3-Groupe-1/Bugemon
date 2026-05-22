package ulb.services.exceptions;

/** Thrown when a requested team cannot be found in the repository. */
public class TeamNotFoundException extends Exception {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
