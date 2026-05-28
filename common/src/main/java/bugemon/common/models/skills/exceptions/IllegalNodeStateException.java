package bugemon.common.models.skills.exceptions;

/**
 * Thrown when an operation on a {@link bugemon.common.models.skills.SkillNode} is attempted in an incompatible state,
 * such as trying to unlock a locked node or unlock a node beyond its maximum level.
 */
public class IllegalNodeStateException extends Exception {
    public IllegalNodeStateException(String message) {
        super(message);
    }
}
