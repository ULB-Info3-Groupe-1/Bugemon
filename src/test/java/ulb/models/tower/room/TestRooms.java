package ulb.models.tower.room;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ulb.controllers.TowerController;
import ulb.models.tower.room.Room.RoomType;
import ulb.models.tower.utils.CombatFactory;

public class TestRooms {
    @Test
    public void testCombatRoomCompletionAndType() {
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, null, false);

        assertEquals(RoomType.COMBAT, combatRoom.getType());
    }

    @Test
    public void testBossCombatRoomType() {
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom bossRoom = new CombatRoom(combatFactory, null, true);

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
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, null, false);
        RewardRoom rewardRoom = new RewardRoom();
        EmptyRoom emptyRoom = new EmptyRoom();
        TowerController towerController = mock(TowerController.class);

        combatRoom.visitIfNotVisited(towerController);
        rewardRoom.visitIfNotVisited(towerController);
        emptyRoom.visitIfNotVisited(towerController);

        verify(towerController).visitCombatRoom(combatRoom);
        verify(towerController).visitRewardRoom(rewardRoom);
        verify(towerController).visitEmptyRoom(emptyRoom);
    }
}
