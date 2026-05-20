package ulb.models.tower;

import java.util.List;
import java.util.Map;

import ulb.models.tower.room.Room;

public class FloorMap {
    private final List<Room> rooms;
    private final Map<Room, RoomPosition> positions;
    private final Map<Room, List<Room>> neighbors;
    private Room currentRoom;

    public FloorMap(List<Room> rooms, Map<Room, RoomPosition> positions, Map<Room, List<Room>> neighbors,
            Room currentRoom) {
        this.rooms = rooms;
        this.positions = positions;
        this.neighbors = neighbors;
        this.currentRoom = currentRoom;
    }

    public List<Room> getAllRooms() {
        return this.rooms;
    }

    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    /**
     * Returns the neighboring rooms, excluding all visited ones (and the current room).
     */
    public List<Room> getReachableRooms() {
        return this.getClickableRooms().stream()
                .filter(r -> !r.isVisited()) // filter out visited rooms
                .toList();
    }

    /**
     * Returns the neighboring rooms, (excluding the current room).
     */
    public List<Room> getClickableRooms() { // TODO: name is bad
        return this.neighbors.get(this.currentRoom).stream()
                .filter(r -> !r.equals(this.currentRoom)) // filter out current room
                .toList();
    }

    public List<Room> getNeighbors(Room room) {
        return this.neighbors.getOrDefault(room, List.of());
    }

    public void movePlayerTo(Room room) {
        // TODO: might need to check if the player can move there (throw if not)
        this.currentRoom.markAsVisited();
        this.currentRoom = room;
    }

    public int getCol(Room room) {
        return this.positions.get(room).col();
    }

    public int getRow(Room room) {
        return this.positions.get(room).row();
    }

    public record RoomPosition(int row, int col) {
    }
}
