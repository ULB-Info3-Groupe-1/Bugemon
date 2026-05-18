package ulb.models.tower.room;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ulb.controllers.TowerController;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.skills.Skill;
import ulb.models.tower.room.Room.RoomType;
import ulb.repositories.InventoryRepository;
import ulb.services.InventoryService;
import ulb.services.SkillService;
import ulb.utils.test.TestUtilsBugemons;

public class TestRooms {

    private static final List<Skill> DEFAULT_SKILLS = List.of();

    @Before
    public void setUp() {
        InventoryService.resetInstance();
        InventoryService.init("TestPlayer", mock(InventoryRepository.class), mock(SkillService.class));
    }

    @After
    public void tearDown() {
        InventoryService.resetInstance();
    }

    @Test
    public void testCombatRoomCompletionAndType() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        List<Bugemon> mockBugemons = new ArrayList<>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        mockBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        CombatRoom combatRoom = new CombatRoom(mockBugemons, playerTeam, DEFAULT_SKILLS, false);

        assertEquals(RoomType.COMBAT, combatRoom.getType());
    }

    @Test
    public void testBossCombatRoomType() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        List<Bugemon> mockBugemons = new ArrayList<>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        mockBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        CombatRoom bossRoom = new CombatRoom(mockBugemons, playerTeam, DEFAULT_SKILLS, true);

        assertEquals(RoomType.BOSS, bossRoom.getType());
        assertTrue(bossRoom.isBoss());
    }

    @Test
    public void testEmptyCompletionSemantics() {
        EmptyRoom emptyRoom = new EmptyRoom();

        assertEquals(RoomType.EMPTY, emptyRoom.getType());
    }

    @Test
    public void testVisitDelegatesToTowerController() {
        BugemonTeam playerTeam = TestUtilsBugemons.createDefaultTeam(3);

        List<Bugemon> mockBugemons = new ArrayList<>(TestUtilsBugemons.createDefaultTeam(6).stream().toList());
        mockBugemons.add(TestUtilsBugemons.createDefaultBugemon("FinalBoss"));
        CombatRoom combatRoom = new CombatRoom(mockBugemons, playerTeam, DEFAULT_SKILLS, false);
        RewardRoom rewardRoom = new RewardRoom();
        EmptyRoom emptyRoom = new EmptyRoom();
        TowerController towerController = mock(TowerController.class);

        if (!combatRoom.isVisited()) {
            combatRoom.visit(towerController);
        }
        if (!rewardRoom.isVisited()) {
            rewardRoom.visit(towerController);
        }
        if (!emptyRoom.isVisited()) {
            emptyRoom.visit(towerController);
        }

        verify(towerController).visitCombatRoom(combatRoom);
        verify(towerController).visitRewardRoom(rewardRoom);
        verify(towerController).visitEmptyRoom(emptyRoom);
    }
}
