package ulb.models.tower.exceptions;

public class FloorLevelAlreadyExistsException extends RuntimeException {
    public FloorLevelAlreadyExistsException(int level) {
        super("A floor with level " + level + " already exists in this tower.");
    }
}
