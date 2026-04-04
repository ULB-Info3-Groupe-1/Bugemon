package ulb.models.no_tower;

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
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.utils.test.TestUtilsBugemons;

public class TestNOTower {

    private static final PlayerService PLAYER_SERVICE_MOCK = mock(PlayerService.class);
    private static final BugemonService BUGEMON_SERVICE_MOCK = mock(BugemonService.class);

    @Before
    public void addBossBugemon() {
        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        when(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons()).thenReturn(testBugemons);
        when(PLAYER_SERVICE_MOCK.getInventory()).thenReturn(new Inventory());
    }

    private void completeCurrentFloor(NOTower noTower) {
        Floor currentFloor = noTower.getCurrentFloor();
        while (!currentFloor.isComplete()) {
            currentFloor.getNextRoom();
        }
    }

    @Test
    public void testNOTowerInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        NOTower noTower = new NOTower(playerTeam, PLAYER_SERVICE_MOCK, BUGEMON_SERVICE_MOCK);

        assertEquals(0, noTower.getCurrentFloorNumber());
        assertFalse(noTower.isFloorComplete());

        for (int expectedFloor = 1; expectedFloor <= 8; expectedFloor++) {
            this.completeCurrentFloor(noTower);
            assertTrue(noTower.isFloorComplete());
            noTower.goToNextFloor();
            assertEquals(expectedFloor, noTower.getCurrentFloorNumber());
        }

        this.completeCurrentFloor(noTower);
        assertTrue(noTower.isFloorComplete());
        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
        assertEquals(8, noTower.getCurrentFloorNumber());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        NOTower noTower = new NOTower(playerTeam, PLAYER_SERVICE_MOCK, BUGEMON_SERVICE_MOCK);

        assertFalse(noTower.isFloorComplete());
    }
}
