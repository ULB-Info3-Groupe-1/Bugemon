package bugemon.server.repositories.exceptions;

/**
 * Exception thrown when a player name or email is not found in the repository, e.g. during login or password
 * verification.
 */
public class IdentifierNotFoundException extends Exception {
    public IdentifierNotFoundException(String message) {
        super(message);
    }

    public IdentifierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
