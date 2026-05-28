package bugemon.common.models.tower;

import java.util.List;
import java.util.Map;

import bugemon.common.models.tower.exceptions.IllegalMoveException;
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.utils.Position;

/**
 * Represents the navigable map for a single tower floor.
 *
 * <p>
 * The map is a tree of {@link Room}s: each room has a {@link Position} (grid coordinates) and a set of neighbors. The
 * player occupies exactly one room at a time ({@code currentRoom}). Movement is only allowed to reachable neighbors;
 * moving out of the current room marks it as visited.
 *
 * <p>
 * Instances are built by {@link bugemon.common.models.tower.utils.FloorMapFactory} and persisted via
 * {@link bugemon.server.repositories.TowerRepository}.
 */
public class FloorMap {
    private final List<Room> rooms;
    private final Map<Room, Position> positions;
    private final Map<Room, List<Room>> neighbors;
    private Room currentRoom;

    /**
     * @param rooms
     *            the complete list of rooms on this floor
     * @param positions
     *            mapping from each room to its grid {@link Position}
     * @param neighbors
     *            adjacency map; each room maps to its directly connected rooms
     * @param currentRoom
     *            the room in which the player currently stands
     */
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
    public List<Room> getClickableRooms() {
        // filter out current room
        return this.neighbors.get(this.currentRoom).stream().filter(r -> !r.equals(this.currentRoom)).toList();
    }

    public List<Room> getNeighbors(Room room) {
        return this.neighbors.getOrDefault(room, List.of());
    }

    /**
     * Moves the player from the current room to {@code room}, marking the previous room as visited.
     *
     * @param room
     *            the destination room
     * @throws IllegalMoveException
     *             if {@code room} is not among the current room's clickable neighbors
     */
    public void movePlayerTo(Room room) throws IllegalMoveException {
        if (!this.getClickableRooms().contains(room)) {
            throw new IllegalMoveException("Moving to the requested room is currently impossible");
        }

        this.currentRoom.markAsVisited();
        this.currentRoom = room;
    }

    /**
     * Forcibly sets the current room without marking any room as visited. Intended for restoring persisted state on
     * load.
     *
     * @param room
     *            the room to set as current
     */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    /**
     * Returns the grid {@link Position} of {@code room}, or {@code null} if the room is not part of this map.
     *
     * @param room
     *            the room to look up
     * @return the room's {@link Position}, or {@code null}
     */
    public Position getPosition(Room room) {
        return this.positions.get(room);
    }
}
