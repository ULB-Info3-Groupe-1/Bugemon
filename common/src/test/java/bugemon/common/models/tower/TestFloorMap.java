package bugemon.common.models.tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.RoomType;
import bugemon.common.models.tower.exceptions.IllegalMoveException;
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.utils.Position;

public class TestFloorMap {

    private Room startRoom;
    private Room neighborRoom;
    private Room isolatedRoom;
    private FloorMap floorMap;

    @Before
    public void setUp() {
        this.startRoom = new Room(RoomType.START);
        this.neighborRoom = new Room(RoomType.COMBAT);
        this.isolatedRoom = new Room(RoomType.EMPTY);

        Map<Room, Position> positions = new HashMap<>();
        positions.put(this.startRoom, new Position(0, 0));
        positions.put(this.neighborRoom, new Position(1, 0));
        positions.put(this.isolatedRoom, new Position(2, 0));

        Map<Room, List<Room>> neighbors = new HashMap<>();
        neighbors.put(this.startRoom, new ArrayList<>(List.of(this.neighborRoom)));
        neighbors.put(this.neighborRoom, new ArrayList<>(List.of(this.startRoom)));
        neighbors.put(this.isolatedRoom, new ArrayList<>());

        List<Room> rooms = List.of(this.startRoom, this.neighborRoom, this.isolatedRoom);
        this.floorMap = new FloorMap(rooms, positions, neighbors, this.startRoom);
    }

    @Test
    public void shouldThrowIllegalMoveException_whenMovingToNonClickableRoom() {
        assertThrows(IllegalMoveException.class, () -> {
            this.floorMap.movePlayerTo(this.isolatedRoom);
        });
    }

    @Test
    public void shouldMoveSuccessfully_whenMovingToClickableRoom() throws IllegalMoveException {
        this.floorMap.movePlayerTo(this.neighborRoom);
        assertEquals(this.neighborRoom, this.floorMap.getCurrentRoom());
    }

    @Test
    public void shouldMarkPreviousRoomAsVisited_afterMove() throws IllegalMoveException {
        this.floorMap.movePlayerTo(this.neighborRoom);
        assertTrue(this.startRoom.isVisited());
    }

    @Test
    public void shouldThrowIllegalMoveException_whenMovingToCurrentRoom() {
        Map<Room, List<Room>> neighborsWithSelf = new HashMap<>();
        List<Room> startNeighbors = new ArrayList<>();
        startNeighbors.add(this.startRoom);
        startNeighbors.add(this.neighborRoom);
        neighborsWithSelf.put(this.startRoom, startNeighbors);
        neighborsWithSelf.put(this.neighborRoom, List.of(this.startRoom));
        neighborsWithSelf.put(this.isolatedRoom, List.of());

        Map<Room, Position> positions = new HashMap<>();
        positions.put(this.startRoom, new Position(0, 0));
        positions.put(this.neighborRoom, new Position(1, 0));
        positions.put(this.isolatedRoom, new Position(2, 0));

        FloorMap mapWithSelfNeighbor = new FloorMap(List.of(this.startRoom, this.neighborRoom, this.isolatedRoom),
                positions, neighborsWithSelf, this.startRoom);

        assertThrows(IllegalMoveException.class, () -> {
            mapWithSelfNeighbor.movePlayerTo(this.startRoom);
        });
    }
}
