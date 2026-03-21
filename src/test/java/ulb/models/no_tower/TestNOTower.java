package ulb.models.no_tower;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.factory.TeamFactory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.*;

public class TestNOTower {
    @Test
    public void testNOTowerInitialization() {
        BugemonTeam playerTeam = TeamFactory.createRandomTeam(3);
        NOTower noTower = new NOTower(playerTeam);

        assertTrue(noTower.getCurrentFloorNumber() == 0);
        assertFalse(noTower.isFloorComplete());

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 1);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 2);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 3);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 4);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 5);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 6);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 7);

        assertTrue(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 8);

        assertFalse(noTower.goToNextFloor());
        assertTrue(noTower.getCurrentFloorNumber() == 9);
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TeamFactory.createRandomTeam(3);
        NOTower noTower = new NOTower(playerTeam);

        assertFalse(noTower.isFloorComplete());
    }
}
