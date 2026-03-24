package ulb.models.no_tower;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.no_tower.room.*;
import ulb.models.trainer.*;
import ulb.services.CombatService;
import ulb.services.PlayerService;

public class TestFloor {

    @Test
    public void testFloorInitialization() {
        BugemonTeam playerTeam = CombatService.createRandomTeam(PlayerService.getAllDefaultBugemons(), 3);
        Trainer playerTrainer = new ManualTrainer(playerTeam);
        Floor floor = new Floor(playerTrainer);

        assertFalse(floor.isComplete());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = CombatService.createRandomTeam(PlayerService.getAllDefaultBugemons(), 3);
        Trainer playerTrainer = new ManualTrainer(playerTeam);
        Floor floor = new Floor(playerTrainer);

        Room firstRoom = floor.getNextRoom();
        assertTrue(firstRoom instanceof CombatRoom);

        Room secondRoom = floor.getNextRoom();
        assertTrue(secondRoom instanceof RewardRoom);

        Room thirdRoom = floor.getNextRoom();
        assertTrue(thirdRoom instanceof CombatRoom);

        Room fourthRoom = floor.getNextRoom();
        assertTrue(fourthRoom instanceof CombatRoom);

        Room fifthRoom = floor.getNextRoom();
        assertTrue(fifthRoom instanceof RewardRoom);

        Room sixthRoom = floor.getNextRoom();
        assertTrue(sixthRoom instanceof CombatRoom);
        CombatRoom bossRoom = (CombatRoom)sixthRoom;
        assertTrue(bossRoom.isBoss());

        assertTrue(floor.isComplete());
    }
}
