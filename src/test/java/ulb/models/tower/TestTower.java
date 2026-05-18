package ulb.models.tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.skills.Skill;
import ulb.repositories.InventoryRepository;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.SkillService;
import ulb.utils.test.TestUtilsBugemons;

public class TestTower {

    private static final BugemonService BUGEMON_SERVICE_MOCK = mock(BugemonService.class);
    private static int currentFloor = 2;
    private static final List<Skill> DEFAULT_SKILLS = List.of();

    @Before
    public void addBossBugemon() {
        InventoryService.resetInstance();
        InventoryService.init("TestPlayer", mock(InventoryRepository.class), mock(SkillService.class));

        List<Bugemon> testBugemons = new ArrayList<Bugemon>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        testBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        // Add boss Bugemon required by Floor.initBossCombatRoom()
        when(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons()).thenReturn(testBugemons);
    }

    @After
    public void tearDown() {
        InventoryService.resetInstance();
    }

    @Test
    public void testTowerInitialization() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        // No tower structure with floor NO2, NO3, NO4, NO5, NO6, NO7, NO8
        Tower noTower = new Tower(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons(), playerTeam, DEFAULT_SKILLS,
                currentFloor);

        assertEquals(2, noTower.getCurrentFloorNumber());
        assertFalse(noTower.isFinished());
        assertEquals(2, noTower.getCurrentFloor().getFloorLevel());
    }

    @Test
    public void testFloorCompletion() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Tower noTower = new Tower(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons(), playerTeam, DEFAULT_SKILLS,
                currentFloor);
        assertFalse(noTower.isFinished());
    }

    @Test
    public void testGoToNextFloorThrowsWhenCurrentFloorIncomplete() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        Tower noTower = new Tower(BUGEMON_SERVICE_MOCK.getAllDefaultBugemons(), playerTeam, DEFAULT_SKILLS,
                currentFloor);
        assertThrows(IllegalStateException.class, noTower::goToNextFloor);
    }
}
