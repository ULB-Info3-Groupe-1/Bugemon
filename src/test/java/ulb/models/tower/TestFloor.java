package ulb.models.tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.skills.Skill;
import ulb.models.tower.room.Room;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.utils.test.TestUtilsBugemons;

public class TestFloor {

    private static final List<Skill> DEFAULT_SKILLS = List.of();
    private static final BugemonService BUGEMON_SERVICE_MOCK = mock(BugemonService.class);
    private static final Inventory INVENTORY_MOCK = mock(Inventory.class);

    @Before
    public void addBossBugemon() {
        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        when(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons()).thenReturn(testBugemons);
    }

    @Test
    public void testFloorInitialization() {
        Floor floor = this.createFloor();

        assertFalse(floor.isComplete());
    }

    @Test
    public void testGetNextRoomsNotEmptyAtStart() {
        Floor floor = this.createFloor();
        List<Room> rooms = floor.getNextRooms();
        assertFalse(rooms.isEmpty());
    }

    @Test
    public void testMoveToChildUpdatesCurrentPosition() {
        Floor floor = this.createFloor();
        FloorNode start = floor.getCurrentPosition();
        FloorNode child = start.getChildren().getFirst();

        floor.moveTo(child);

        assertEquals(child, floor.getCurrentPosition());
    }

    @Test
    public void testMoveBackToParentUpdatesCurrentPosition() {
        Floor floor = this.createFloor();
        FloorNode start = floor.getCurrentPosition();
        FloorNode child = start.getChildren().getFirst();
        floor.moveTo(child);

        floor.moveTo(start);

        assertEquals(start, floor.getCurrentPosition());
    }

    @Test
    public void testMoveToNonAdjacentNodeKeepsCurrentPosition() {
        Floor floor = this.createFloor();
        FloorNode root = floor.getCurrentPosition();
        FloorNode firstChild = root.getChildren().get(0);
        FloorNode siblingChild = root.getChildren().get(1);
        floor.moveTo(firstChild);

        floor.moveTo(siblingChild);

        assertEquals(firstChild, floor.getCurrentPosition());
        assertNotEquals(siblingChild, floor.getCurrentPosition());
    }

    @Test
    public void testReachableNodesFromChildContainParent() {
        Floor floor = this.createFloor();
        FloorNode root = floor.getCurrentPosition();
        FloorNode child = root.getChildren().getFirst();
        floor.moveTo(child);

        List<FloorNode> reachable = floor.getReachableNodes();
        assertTrue(reachable.contains(root));
    }

    @Test
    public void testGetFloorNodesContainsRootAndCurrentPosition() {
        Floor floor = this.createFloor();
        FloorNode root = floor.getCurrentPosition();
        FloorNode child = root.getChildren().getFirst();
        floor.moveTo(child);

        List<FloorNode> allNodes = floor.getFloorNodes();

        assertTrue(allNodes.contains(root));
        assertTrue(allNodes.contains(floor.getCurrentPosition()));
        assertTrue(allNodes.size() >= floor.getReachableNodes().size());
    }

    private Floor createFloor() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Trainer playerTrainer = new ManualTrainer(playerTeam, INVENTORY_MOCK);
        return new Floor(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons(), DEFAULT_SKILLS, playerTrainer, 1);
    }
}
