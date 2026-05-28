package bugemon.common.models.player.exceptions;

/**
 * Thrown when an attempt is made to replace a Bugemon's attack with one that violates replacement constraints, such as
 * replacing an attack that does not belong to that Bugemon.
 */
public class IllegalAttackReplacementException extends Exception {
    public IllegalAttackReplacementException(String message) {
        super(message);
    }
}
