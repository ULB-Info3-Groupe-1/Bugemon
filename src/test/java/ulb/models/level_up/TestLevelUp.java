/**
 * File name : TestLevelUp.java
 * Description : Test class for the LevelUp class, which handles the level-up process of a Bugemon
 * @author Gouverneur Martin
 * @date 09 mar. 2026
 * @version 1.0
 */

package ulb.models.level_up;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;

public class TestLevelUp {

    @Test
    public void testChoicesTotalTenPoints() {
        Bugemon base = new Bugemon("a", 1, 1, 1, 1, null, null, null, true, false);
        PlayerBugemon bugemon = new PlayerBugemon(base, 1, 1, null, null);

        LevelUp levelUp = new LevelUp(bugemon);
        for (Upgrade upgrade : levelUp.upgrades()) {
            // 1 point is worth 2 HP or 1 Initiative, while Attack and Defense are worth 1
            // point
            // each
            int pointsHp = upgrade.hp() / 2;
            int pointsAttack = upgrade.attack();
            int pointsDefense = upgrade.defense();
            int pointsInitiative = upgrade.initiative() / 2;

            int totalPoints = pointsHp + pointsAttack + pointsDefense + pointsInitiative;

            assertEquals(10, totalPoints);

            assertEquals(0, upgrade.hp() % 2);
            assertEquals(0, upgrade.initiative() % 2);
            assertTrue(
                    upgrade.hp() >= 0 && upgrade.attack() >= 0 && upgrade.defense() >= 0 && upgrade.initiative() >= 0);
        }
    }

    @Test
    public void testGetOutOfBoundsThrows() {
        Bugemon base = new Bugemon("a", 1, 1, 1, 1, null, null, null, true, false);
        PlayerBugemon bugemon = new PlayerBugemon(base, 1, 1, null, null);
        LevelUp levelUp = new LevelUp(bugemon);

        int invalidIndex = levelUp.numUpgrades();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            levelUp.get(invalidIndex);
        });
    }
}
