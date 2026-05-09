package ulb.models.tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.RoomVisitor;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.TowerService;
import ulb.utils.test.TestUtilsBugemons;

public class TestTower {

    private static final BugemonService BUGEMON_SERVICE_MOCK = mock(BugemonService.class);
    private static final InventoryService INVENTORY_SERVICE_MOCK = mock(InventoryService.class);
    private static final TowerService TOWER_SERVICE_MOCK = mock(TowerService.class);

    @Before
    public void addBossBugemon() {
        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        when(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons()).thenReturn(testBugemons);
        when(TOWER_SERVICE_MOCK.getCurrentFloor()).thenReturn(2);
    }

    private void completeCurrentFloor(Tower noTower) {
        Floor floor = noTower.getCurrentFloor();
        for (FloorNode node : floor.getFloorNodes()) {
            if (node.getRoom() != null && node.getRoom().getType() == Room.RoomType.BOSS) {
                node.setRoom(new Room() {
                    @Override
                    public boolean hasPlayerWon() {
                        return true;
                    }

                    @Override
                    public void visitIfNotVisited(RoomVisitor roomVisitor) {
                        // Do nothing for this test
                    }

                    @Override
                    public RoomType getType() {
                        return RoomType.BOSS;
                    }
                });
                break;
            }
        }
    }

    @Test
    public void testTowerInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        // No tower structure with floor NO2, NO3, NO4, NO5, NO6, NO7, NO8
        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, TOWER_SERVICE_MOCK);

        assertEquals(2, noTower.getCurrentFloorNumber());
        assertFalse(noTower.isCompleted());

        for (int expectedFloor = 2; expectedFloor <= 7; expectedFloor++) {
            assertEquals(expectedFloor, noTower.getCurrentFloorNumber());
            this.completeCurrentFloor(noTower);
            assertTrue(noTower.isCurrentFloorComplete());
            noTower.goToNextFloor();
        }

        assertEquals(8, noTower.getCurrentFloorNumber());
        this.completeCurrentFloor(noTower);
        assertTrue(noTower.isCompleted());
        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, TOWER_SERVICE_MOCK);

        assertFalse(noTower.isCompleted());
    }

    @Test
    public void testGoToNextFloorThrowsWhenCurrentFloorIncomplete() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        when(TOWER_SERVICE_MOCK.getCurrentFloor()).thenReturn(2);
        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, TOWER_SERVICE_MOCK);

        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
    }
}
