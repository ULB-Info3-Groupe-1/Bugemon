package ulb.repositories.exceptions;

/** Thrown when saving a team with a name already used by the current player. */
public class TeamNameAlreadyExistsException extends Exception {
    public TeamNameAlreadyExistsException(String message) {
        super(message);
    }
}
