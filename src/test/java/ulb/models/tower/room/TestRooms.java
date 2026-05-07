package ulb.models.tower.room;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ulb.controllers.combat.TowerController;
import ulb.models.tower.room.Room.RoomType;
import ulb.models.tower.utils.TowerCombatFactory;
import ulb.services.BugemonService;

public class TestRooms {
    @Test
    public void testCombatRoomCompletionAndType() {
        TowerCombatFactory combatFactory = mock(TowerCombatFactory.class);
        BugemonService bugemonService = mock(BugemonService.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, bugemonService, false);

        assertEquals(RoomType.COMBAT, combatRoom.getType());
    }

    @Test
    public void testBossCombatRoomType() {
        TowerCombatFactory combatFactory = mock(TowerCombatFactory.class);
        BugemonService bugemonService = mock(BugemonService.class);
        CombatRoom bossRoom = new CombatRoom(combatFactory, bugemonService, true);

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
        TowerCombatFactory combatFactory = mock(TowerCombatFactory.class);
        BugemonService bugemonService = mock(BugemonService.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, bugemonService, false);
        RewardRoom rewardRoom = new RewardRoom();
        EmptyRoom emptyRoom = new EmptyRoom();
        TowerController towerController = mock(TowerController.class);

        combatRoom.visit(towerController);
        rewardRoom.visit(towerController);
        emptyRoom.visit(towerController);

        verify(towerController).visitCombatRoom(combatRoom);
        verify(towerController).visitRewardRoom(rewardRoom);
        verify(towerController).visitEmptyRoom(emptyRoom);
    }
}
