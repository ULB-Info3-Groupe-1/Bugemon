package ulb.models.no_tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestNOTower {

    @Test
    public void testNOTowerInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        NOTower noTower = new NOTower(playerTeam);

        assertEquals(0, noTower.getCurrentFloorNumber());
        assertFalse(noTower.isFloorComplete());

        assertTrue(noTower.goToNextFloor());
        assertEquals(1, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(2, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(3, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(4, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(5, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(6, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(7, noTower.getCurrentFloorNumber());

        assertTrue(noTower.goToNextFloor());
        assertEquals(8, noTower.getCurrentFloorNumber());

        assertFalse(noTower.goToNextFloor());
        assertEquals(9, noTower.getCurrentFloorNumber());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        NOTower noTower = new NOTower(playerTeam);

        assertFalse(noTower.isFloorComplete());
    }
}
