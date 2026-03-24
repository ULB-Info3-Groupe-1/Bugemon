package ulb.models.no_tower;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.no_tower.room.*;
import ulb.models.trainer.*;
import ulb.services.PlayerService;
import ulb.utils.test.TestUtilsBugemons;

public class TestFloor {

    private PlayerService getPlayerServiceMock() {
        PlayerService playerServiceMock = mock(PlayerService.class);
        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("finalboss"));
        when(playerServiceMock.getAllDefaultBugemons()).thenReturn(testBugemons);
        return playerServiceMock;
    }

    @Test
    public void testFloorInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Trainer playerTrainer = new ManualTrainer(playerTeam);
        
        Floor floor = new Floor(playerTrainer, getPlayerServiceMock());

        assertFalse(floor.isComplete());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Trainer playerTrainer = new ManualTrainer(playerTeam);        
        
        Floor floor = new Floor(playerTrainer, getPlayerServiceMock());

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
