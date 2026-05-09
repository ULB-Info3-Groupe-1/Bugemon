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
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.utils.test.TestUtilsBugemons;

public class TestTower {

    private static final BugemonService BUGEMON_SERVICE_MOCK = mock(BugemonService.class);
    private static final InventoryService INVENTORY_SERVICE_MOCK = mock(InventoryService.class);

    @Before
    public void addBossBugemon() {
        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        when(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons()).thenReturn(testBugemons);
    }

    private void completeCurrentFloor(Tower noTower) {
        Floor floor = noTower.getCurrentFloor();
        this.navigateToBoss(floor);
    }

    private boolean navigateToBoss(Floor floor) {
        if (floor.isComplete()) {
            return true;
        }
        FloorNode current = floor.getCurrentPosition();
        for (FloorNode child : current.getChildren()) {
            floor.moveTo(child);
            if (this.navigateToBoss(floor)) {
                return true;
            }
            floor.moveTo(current);
        }
        return false;
    }

    @Test
    public void testTowerInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        // Tower structure with floors 2 to 8
        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, 2);

        assertEquals(2, noTower.getCurrentFloorNumber());
        assertFalse(noTower.isFloorComplete());

        for (int expectedFloor = 2; expectedFloor <= 7; expectedFloor++) {
            assertEquals(expectedFloor, noTower.getCurrentFloorNumber());
            this.completeCurrentFloor(noTower);
            assertTrue(noTower.isFloorComplete());
            noTower.goToNextFloor();
        }

        assertEquals(8, noTower.getCurrentFloorNumber());
        this.completeCurrentFloor(noTower);
        assertTrue(noTower.isFloorComplete());
        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, 2);

        assertFalse(noTower.isFloorComplete());
    }

    @Test
    public void testGoToNextFloorThrowsWhenCurrentFloorIncomplete() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        Tower noTower = new Tower(playerTeam, BUGEMON_SERVICE_MOCK, INVENTORY_SERVICE_MOCK, 2);

        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
    }
}
