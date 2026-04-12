package ulb.models.tower.room;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ulb.controllers.combat.TowerController;
import ulb.models.tower.utils.CombatFactory;

public class TestRooms {
    @Test
    public void testCombatRoomCompletionAndType() {
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, 2, false);

        assertFalse(combatRoom.isCompleted());
        assertEquals(RoomType.COMBAT, combatRoom.getType());

        combatRoom.markCompleted();
        assertTrue(combatRoom.isCompleted());
    }

    @Test
    public void testBossCombatRoomType() {
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom bossRoom = new CombatRoom(combatFactory, 5, true);

        assertEquals(RoomType.BOSS, bossRoom.getType());
        assertTrue(bossRoom.isBoss());
    }

    @Test
    public void testEmptyCompletionSemantics() {
        EmptyRoom emptyRoom = new EmptyRoom();

        assertFalse(emptyRoom.isCompleted());
        assertEquals(RoomType.EMPTY, emptyRoom.getType());
    }

    @Test
    public void testVisitDelegatesToTowerController() {
        CombatFactory combatFactory = mock(CombatFactory.class);
        CombatRoom combatRoom = new CombatRoom(combatFactory, 1, false);
        BonusRoom bonusRoom = new BonusRoom();
        EmptyRoom emptyRoom = new EmptyRoom();
        TowerController towerController = mock(TowerController.class);

        combatRoom.visit(towerController);
        bonusRoom.visit(towerController);
        emptyRoom.visit(towerController);

        verify(towerController).handleCombatRoom(combatRoom);
        verify(towerController).handleBonusRoom(bonusRoom);
        verify(towerController).handleEmptyRoom(emptyRoom);
    }
}
