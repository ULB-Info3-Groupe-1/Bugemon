package bugemon.common.models.tower.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.Configuration;
import bugemon.common.RoomType;
import bugemon.common.models.tower.FloorMap;
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.utils.Position;

public class TestFloorFactory {

    private static final int SEED = 42;
    private static final int FLOOR = 2;

    private FloorMap floorMap;
    private Room startRoom;
    private List<Room> allRooms;
    private Map<Room, Integer> depthPerRoom;
    private Map<Room, Room> parentPerRoom;
    private int maxDepth;
    private List<Room> deepestRooms;

    @Before
    public void setup() {
        FloorMapFactory factory = new FloorMapFactory(SEED);
        this.floorMap = factory.create(FLOOR);
        this.startRoom = this.floorMap.getCurrentRoom();

        this.allRooms = new ArrayList<>();
        this.depthPerRoom = new HashMap<>();
        this.parentPerRoom = new HashMap<>();
        this.maxDepth = 0;
        this.deepestRooms = new ArrayList<>();

        Deque<Room> queue = new ArrayDeque<>();
        Set<Room> visited = new HashSet<>();
        queue.add(this.startRoom);
        visited.add(this.startRoom);
        this.depthPerRoom.put(this.startRoom, 0);
        this.parentPerRoom.put(this.startRoom, null);

        while (!queue.isEmpty()) {
            Room room = queue.poll();
            int depth = this.depthPerRoom.get(room);
            this.allRooms.add(room);

            if (depth > this.maxDepth) {
                this.maxDepth = depth;
                this.deepestRooms = new ArrayList<>();
                this.deepestRooms.add(room);
            } else if (depth == this.maxDepth) {
                this.deepestRooms.add(room);
            }

            for (Room neighbor : this.floorMap.getNeighbors(room)) {
                if (visited.add(neighbor)) {
                    this.depthPerRoom.put(neighbor, depth + 1);
                    this.parentPerRoom.put(neighbor, room);
                    queue.add(neighbor);
                }
            }
        }
    }

    @Test
    public void testAllRoomsHaveTypes() {
        for (Room room : this.allRooms) {
            assertThat(room.getType()).isNotNull();
        }
    }

    @Test
    public void testAllRoomsInGrid() {
        for (Room room : this.allRooms) {
            Position pos = this.floorMap.getPosition(room);
            assertThat(pos.x()).isBetween(0, Configuration.FloorMap.GRID_SIZE - 1);
            assertThat(pos.y()).isBetween(0, Configuration.FloorMap.GRID_SIZE - 1);
        }
    }

    @Test
    public void testStartRoomIsStart() {
        assertThat(this.startRoom.getType()).isEqualTo(RoomType.START);
    }

    @Test
    public void testMaxDepth() {
        assertThat(this.maxDepth).isLessThanOrEqualTo(Configuration.FloorMap.MAX_DEPTH);
    }

    @Test
    public void testBossAtDeepestLevel() {
        Room bossRoom = this.allRooms.stream().filter(r -> r.getType() == RoomType.BOSS).findFirst().orElse(null);
        assertThat(bossRoom).isNotNull();
        assertThat(this.deepestRooms).contains(bossRoom);
    }

    @Test
    public void testOnlyOneBossRoom() {
        long bossCount = this.allRooms.stream().filter(r -> r.getType() == RoomType.BOSS).count();
        assertThat(bossCount).isEqualTo(1);
    }

    @Test
    public void testRewardRoomCountInRange() {
        long rewardCount = this.allRooms.stream().filter(r -> r.getType() == RoomType.REWARD).count();
        assertThat(rewardCount).isBetween((long) Configuration.FloorMap.MIN_REWARD,
                (long) Configuration.FloorMap.MAX_REWARD);
    }

    @Test
    public void testStartRoomBranchCountInRange() {
        int branches = this.floorMap.getNeighbors(this.startRoom).size();
        assertThat(branches).isBetween(Configuration.FloorMap.MIN_BRANCHES, Configuration.FloorMap.MAX_BRANCHES);
    }

    @Test
    public void testRewardRoomHasCombatAncestor() {
        for (Room room : this.allRooms) {
            if (room.getType() != RoomType.REWARD) {
                continue;
            }
            boolean hasCombatAncestor = false;
            Room current = this.parentPerRoom.get(room);
            while (current != null && current != this.startRoom) {
                if (current.getType() == RoomType.COMBAT) {
                    hasCombatAncestor = true;
                    break;
                }
                current = this.parentPerRoom.get(current);
            }
            assertThat(hasCombatAncestor).as("RewardRoom at (%d,%d) has no COMBAT ancestor",
                    this.floorMap.getPosition(room).x(), this.floorMap.getPosition(room).y()).isTrue();
        }
    }
}
