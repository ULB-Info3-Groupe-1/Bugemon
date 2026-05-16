package ulb.models.tower.room;

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

    public enum RoomType {
        COMBAT,
        BOSS,
        REWARD,
        EMPTY,
    }
}
