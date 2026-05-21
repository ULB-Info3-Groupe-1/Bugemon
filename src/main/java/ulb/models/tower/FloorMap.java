package ulb.models.tower;

import java.util.List;
import java.util.Map;

import ulb.models.tower.exceptions.IllegalMoveException;
import ulb.models.tower.room.Room;
import ulb.models.utils.Position;

public class FloorMap {
    private final List<Room> rooms;
    private final Map<Room, Position> positions;
    private final Map<Room, List<Room>> neighbors;
    private Room currentRoom;

    public FloorMap(List<Room> rooms, Map<Room, Position> positions, Map<Room, List<Room>> neighbors,
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

    public List<Room> getVisitedRooms() {
        return this.rooms.stream().filter(Room::isVisited).toList();
    }

    /**
     * Returns the neighboring rooms, excluding all visited ones (and the current room).
     */
    public List<Room> getReachableRooms() {
        return this.getClickableRooms().stream().filter(r -> !r.isVisited()) // filter out visited rooms
                .toList();
    }

    /**
     * Returns the neighboring rooms, (excluding the current room).
     */
    public List<Room> getClickableRooms() { // TODO: name is bad
        return this.neighbors.get(this.currentRoom).stream().filter(r -> !r.equals(this.currentRoom)) // filter out
                                                                                                      // current room
                .toList();
    }

    public List<Room> getNeighbors(Room room) {
        return this.neighbors.getOrDefault(room, List.of());
    }

    public void movePlayerTo(Room room) throws IllegalMoveException {
        if (!this.getClickableRooms().contains(room)) {
            throw new IllegalMoveException("Moving to the requested room is currently impossible");
        }

        this.currentRoom.markAsVisited();
        this.currentRoom = room;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Position getPosition(Room room) {
        return this.positions.get(room);
    }
}
