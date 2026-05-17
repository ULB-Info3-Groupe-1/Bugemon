package ulb.models.tower.exceptions;

public class FloorLevelNotFoundException extends RuntimeException {
    public FloorLevelNotFoundException(int level) {
        super("The NO floor " + level + " does not exist in the tower.");
    }
}
