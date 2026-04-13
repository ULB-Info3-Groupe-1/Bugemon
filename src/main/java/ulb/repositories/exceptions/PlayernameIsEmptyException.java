package ulb.repositories.exceptions;

/** Thrown when a player name is null or blank. */
public class PlayernameIsEmptyException extends Exception {
    public PlayernameIsEmptyException(String message) {
        super(message);
    }
}
