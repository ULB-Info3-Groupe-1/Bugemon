package ulb.models.tower.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.room.Room.RoomType;

public class TestFloorFactory {

    private Floor floor;
    private FloorNode root;

    private List<FloorNode> allNodes;
    private int maxDepth;
    private List<FloorNode> deepestNodes;
    private long rewardCount;
    private long bossCount;

    @Before
    public void setup() {
        FloorFactory factory = new FloorFactory(new Random(42));
        this.floor = factory.create();
        this.root = this.floor.getRoot();

        this.allNodes = new ArrayList<>();
        this.deepestNodes = new ArrayList<>();
        this.maxDepth = 0;

        Map<FloorNode, Integer> depthPerNode = new HashMap<>();
        Set<FloorNode> visited = new HashSet<>();

        Deque<FloorNode> queue = new ArrayDeque<>();
        queue.add(this.root);
        depthPerNode.put(this.root, 0);
        visited.add(this.root);

        while (!queue.isEmpty()) {
            FloorNode node = queue.poll();
            int depth = depthPerNode.getOrDefault(node, 0);

            this.allNodes.add(node);
            if (depth > this.maxDepth) {
                this.maxDepth = depth;
                this.deepestNodes = new ArrayList<>();
                this.deepestNodes.add(node);
            } else if (depth == this.maxDepth) {
                this.deepestNodes.add(node);
            }

            for (FloorNode child : node.getChildren()) {
                if (visited.add(child)) {
                    depthPerNode.put(child, depth + 1);
                    queue.add(child);
                }
            }
        }

        this.rewardCount = this.allNodes.stream()
                .filter(n -> n.getRoom() != null && n.getRoom().getType() == RoomType.REWARD).count();
        this.bossCount = this.allNodes.stream()
                .filter(n -> n.getRoom() != null && n.getRoom().getType() == RoomType.BOSS).count();
    }

    @Test
    public void testAllNodesHaveRooms() {
        for (FloorNode node : this.allNodes) {
            assertNotNull("Node at (" + node.getX() + "," + node.getY() + ") has no room", node.getRoom());
        }
    }

    @Test
    public void testAllNodesInGrid() {
        for (FloorNode node : this.allNodes) {
            assertTrue("Node x=" + node.getX() + " out of grid [0," + FloorFactory.GRID_SIZE + ")",
                    node.getX() >= 0 && node.getX() < FloorFactory.GRID_SIZE);
            assertTrue("Node y=" + node.getY() + " out of grid [0," + FloorFactory.GRID_SIZE + ")",
                    node.getY() >= 0 && node.getY() < FloorFactory.GRID_SIZE);
        }
    }

    @Test
    public void testRootIsEmptyRoom() {
        assertNotNull(this.root.getRoom());
        assertEquals(RoomType.EMPTY, this.root.getRoom().getType());
    }

    @Test
    public void testMaxDepth() {
        assertTrue("Floor depth " + this.maxDepth + " exceeds MAX_DEPTH=" + FloorFactory.MAX_DEPTH,
                this.maxDepth <= FloorFactory.MAX_DEPTH);
    }

    @Test
    public void testBossAtDeepestLevel() {
        FloorNode bossNode = this.allNodes.stream()
                .filter(n -> n.getRoom() != null && n.getRoom().getType() == RoomType.BOSS).findFirst().orElse(null);
        assertNotNull("Boss node must exist", bossNode);
        assertTrue("Boss node must be among the deepest nodes", this.deepestNodes.contains(bossNode));
    }

    @Test
    public void testOnlyOneBossRoom() {
        assertEquals(1, this.bossCount);
    }

    @Test
    public void testRewardRoomCountInRange() {
        assertTrue("Too few RewardRooms: " + this.rewardCount + " < MIN_REWARD=" + FloorFactory.MIN_REWARD,
                this.rewardCount >= FloorFactory.MIN_REWARD);
        assertTrue("Too many RewardRooms: " + this.rewardCount + " > MAX_REWARD=" + FloorFactory.MAX_REWARD,
                this.rewardCount <= FloorFactory.MAX_REWARD);
    }

    @Test
    public void testRootBranchCountInRange() {
        int branches = this.root.getChildren().size();
        assertTrue("Root has " + branches + " branches, expected >= MIN_BRANCHES=" + FloorFactory.MIN_BRANCHES,
                branches >= FloorFactory.MIN_BRANCHES);
        assertTrue("Root has " + branches + " branches, expected <= MAX_BRANCHES=" + FloorFactory.MAX_BRANCHES,
                branches <= FloorFactory.MAX_BRANCHES);
    }

    @Test
    public void testRewardRoomHasCombatRoomParent() {
        for (FloorNode node : this.allNodes) {
            if (node.getRoom() != null && node.getRoom().getType() == RoomType.REWARD) {
                assertTrue("RewardRoom at (" + node.getX() + "," + node.getY() + ") has no parent",
                        node.getParent().isPresent());
                assertEquals("RewardRoom at (" + node.getX() + "," + node.getY() + ") parent is not a CombatRoom",
                        RoomType.COMBAT, node.getParent().orElseThrow().getRoom().getType());
            }
        }
    }
}
