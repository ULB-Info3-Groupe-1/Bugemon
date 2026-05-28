package bugemon.common.models.tower.room;

import bugemon.common.RoomType;

/**
 * A single room on a tower floor, characterised by its {@link bugemon.common.RoomType} and visited status.
 *
 * <p>
 * Rooms are created by {@link bugemon.common.models.tower.utils.FloorMapFactory} and are not reused across floors. Once marked
 * visited via {@link #markAsVisited()}, the state cannot be reversed.
 */
public class Room {
    private final RoomType type;
    private boolean visited;

    public Room(RoomType type) {
        this.type = type;
        this.visited = false;
    }

    public RoomType getType() {
        return this.type;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void markAsVisited() {
        this.visited = true;
    }
}
