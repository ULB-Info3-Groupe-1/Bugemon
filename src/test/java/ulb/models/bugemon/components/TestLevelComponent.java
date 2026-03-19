package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class TestLevelComponent {
    @Test
    public void testCreation() {
        LevelComponent levelComponent = new LevelComponent(0, 1);
        assertEquals(1, levelComponent.getLevel());
        assertEquals(0, levelComponent.getXp());
    }

    @Test
    public void testCreationNegXp() {
        assertThrows(IllegalArgumentException.class, () -> { new LevelComponent(-1, 1); });
    }

    @Test
    public void testCreationZeroLevel() {
        assertThrows(IllegalArgumentException.class, () -> { new LevelComponent(0, 0); });
    }

    @Test
    public void testCreationNegLevel() {
        assertThrows(IllegalArgumentException.class, () -> { new LevelComponent(0, -1); });
    }

    @Test
    public void testAddXp() {
        LevelComponent levelComponent = new LevelComponent(0, 1);
        int numLevelUps = 0;

        // not enough to reach level 2
        numLevelUps = levelComponent.addXp(49);

        assertEquals(1, levelComponent.getLevel());
        assertEquals(49, levelComponent.getXp());
        assertEquals(0, numLevelUps);

        // enough to reach level 2 now
        numLevelUps = levelComponent.addXp(1);

        assertEquals(2, levelComponent.getLevel());
        assertEquals(0, levelComponent.getXp());
        assertEquals(1, numLevelUps);
    }

    @Test
    public void testAddXpTwoLevelUps() {
        LevelComponent levelComponent = new LevelComponent(0, 1);
        int numLevelUps = 0;

        // enough to reach level 2
        numLevelUps = levelComponent.addXp(50);

        assertEquals(2, levelComponent.getLevel());
        assertEquals(0, levelComponent.getXp());
        assertEquals(1, numLevelUps);

        // enough to reach level 3
        numLevelUps = levelComponent.addXp(150);

        assertEquals(3, levelComponent.getLevel());
        assertEquals(0, levelComponent.getXp());
        assertEquals(1, numLevelUps);
    }

    @Test
    public void testAddXpTwoLevelUpsAtOnce() {
        LevelComponent levelComponent = new LevelComponent(0, 1);
        int numLevelUps = 0;

        // enough to reach level 3
        numLevelUps = levelComponent.addXp(200);

        assertEquals(3, levelComponent.getLevel());
        assertEquals(0, levelComponent.getXp());
        assertEquals(2, numLevelUps);
    }
}
