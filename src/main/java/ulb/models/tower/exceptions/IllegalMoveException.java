package ulb.models.tower.exceptions;

/**
 * Thrown when the player attempts to move to a room that is not a reachable neighbor of the current room in the
 * {@link ulb.models.tower.FloorMap}.
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException(String message) {
        super(message);
    }
}
