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

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.utils.test.TestUtilsBugemons;

public class TestLevelUp {
    private Bugemon bugemon;

    @Test
    public void testChoicesTotalTenPoints() {
        bugemon = TestUtilsBugemons.createDefaultBugemon("1");

        LevelUp levelUp = new LevelUp(bugemon);
        List<Upgrade> choices = levelUp.getChoices();

        for (Upgrade choice : choices) {
            // 1 point is worth 2 HP or 1 Initiative, while Attack and Defense are worth 1 point
            // each
            int pointsHp = choice.hp() / 2;
            int pointsAttack = choice.attack();
            int pointsDefense = choice.defense();
            int pointsInitiative = choice.initiative() / 2;

            int totalPoints = pointsHp + pointsAttack + pointsDefense + pointsInitiative;

            assertEquals(10, totalPoints);

            assertEquals(0, choice.hp() % 2);
            assertEquals(0, choice.initiative() % 2);
            assertTrue(choice.hp() >= 0 && choice.attack() >= 0 && choice.defense() >= 0
                    && choice.initiative() >= 0);
        }
    }
}
