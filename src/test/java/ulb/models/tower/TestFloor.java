package ulb.models.tower;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.tower.room.Room;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.utils.test.TestUtilsBugemons;

public class TestFloor {

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

    @Test
    public void testFloorInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Trainer playerTrainer = new ManualTrainer(playerTeam, PLAYER_SERVICE_MOCK.getInventory());

        Floor floor = new Floor(playerTrainer, BUGEMON_SERVICE_MOCK);

        assertFalse(floor.isComplete());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Trainer playerTrainer = new ManualTrainer(playerTeam, PLAYER_SERVICE_MOCK.getInventory());

        Floor floor = new Floor(playerTrainer, BUGEMON_SERVICE_MOCK);

        List<Room> rooms = floor.getNextRooms();
        assertFalse(rooms.isEmpty());
    }
}
