/**
 * File name : TestLevelUp.java
 * Description : Test class for the LevelUp class, which handles the level-up process of a Bugemon
 * @author Gouverneur Martin
 * @date 09 mar. 2026
 * @version 1.0
 */

package ulb.models.level_up;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.utils.test.TestUtilsBugemons;

public class TestLevelUp {
    private Bugemon bugemon;

    @Test
    public void testChoicesTotalTenPoints() {
        this.bugemon = TestUtilsBugemons.createDefaultBugemon("1");

        LevelUp levelUp = new LevelUp(this.bugemon);
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
}
