package ulb.models.tower.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.tower.FloorNode;
import ulb.models.tower.room.BonusRoom;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.EmptyRoom;

public class TestFloorGenerator {
    private static final CombatFactory COMBAT_FACTORY_MOCK = mock(CombatFactory.class);

    private FloorGenerator floorGenerator;
    private FloorNode root;

    // Populated during BFS in setup
    private List<FloorNode> allNodes;
    private List<FloorNode> deepestNodes;
    private List<FloorNode> combatNodes;
    private int deepestLevel;
    private int bonusCount;

    @Before
    public void setup() {
        this.floorGenerator = new FloorGenerator(COMBAT_FACTORY_MOCK, 0);
        this.root = this.floorGenerator.getRoot();

        this.allNodes = new ArrayList<>();
        this.combatNodes = new ArrayList<>();
        this.bonusCount = 0;
        this.deepestLevel = 0;

        Deque<FloorNode> queue = new LinkedList<>();
        queue.add(this.root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            this.deepestNodes = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                FloorNode node = queue.poll();
                this.allNodes.add(node);
                this.deepestNodes.add(node);

                if (node.getRoom() instanceof CombatRoom combatRoom && !combatRoom.isBoss()) {
                    this.combatNodes.add(node);
                } else if (node.getRoom() instanceof BonusRoom) {
                    this.bonusCount++;
                }

                queue.addAll(node.getChildren());
            }

            this.deepestLevel++;
        }
    }

    public void testAllNodesHaveRooms() {
        for (FloorNode node : this.allNodes) {
            assertNotNull("Node at (" + node.getX() + "," + node.getY() + ") has no room", node.getRoom());
        }
    }

    @Test
    public void testAllNodesInGrid() {
        for (FloorNode node : this.allNodes) {
            assertTrue("Node x=" + node.getX() + " out of grid [0," + FloorGenerator.GRID_SIZE + ")",
                    node.getX() >= 0 && node.getX() < FloorGenerator.GRID_SIZE);
            assertTrue("Node y=" + node.getY() + " out of grid [0," + FloorGenerator.GRID_SIZE + ")",
                    node.getY() >= 0 && node.getY() < FloorGenerator.GRID_SIZE);
        }
    }

    @Test
    public void testRootIsEmptyRoom() {
        assertTrue("Root must be an EmptyRoom", this.root.getRoom() instanceof EmptyRoom);
    }

    @Test
    public void testMaxDepth() {
        assertTrue("Floor depth " + (this.deepestLevel - 1) + " exceeds MAX_DEPTH=" + FloorGenerator.MAX_DEPTH,
                this.deepestLevel - 1 <= FloorGenerator.MAX_DEPTH);
    }

    @Test
    public void testBossAtDeepestLevel() {
        assertTrue("Boss node must be among the deepest nodes",
                this.deepestNodes.contains(this.floorGenerator.getBossNode()));
    }

    public void testOnlyOneBossRoom() {
        long bossCount = this.allNodes.stream().filter(n -> n.getRoom() instanceof CombatRoom cr && cr.isBoss())
                .count();
        assertTrue("Expected exactly 1 boss room, found " + bossCount, bossCount == 1);
    }

    @Test
    public void testBonusRoomCountInRange() {
        assertTrue("Too few BonusRooms: " + this.bonusCount + " < MIN_BONUS=" + FloorGenerator.MIN_BONUS,
                this.bonusCount >= FloorGenerator.MIN_BONUS);
        assertTrue("Too many BonusRooms: " + this.bonusCount + " > MAX_BONUS=" + FloorGenerator.MAX_BONUS,
                this.bonusCount <= FloorGenerator.MAX_BONUS);
    }

    @Test
    public void testRootBranchCountInRange() {
        int branches = this.root.getChildren().size();
        assertTrue("Root has " + branches + " branches, expected >= MIN_BRANCHES=" + FloorGenerator.MIN_BRANCHES,
                branches >= FloorGenerator.MIN_BRANCHES);
        assertTrue("Root has " + branches + " branches, expected <= MAX_BRANCHES=" + FloorGenerator.MAX_BRANCHES,
                branches <= FloorGenerator.MAX_BRANCHES);
    }

    @Test
    public void testBonusRoomHasCombatRoomParent() {
        for (FloorNode node : this.allNodes) {
            if (node.getRoom() instanceof BonusRoom) {
                assertTrue("BonusRoom at (" + node.getX() + "," + node.getY() + ") has no parent",
                        node.getParent().isPresent());
                assertTrue("BonusRoom at (" + node.getX() + "," + node.getY() + ") parent is not a CombatRoom",
                        node.getParent().get().getRoom() instanceof CombatRoom);
            }
        }
    }
}
