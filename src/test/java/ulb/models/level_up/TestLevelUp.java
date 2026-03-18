/**
 * File name : TestLevelUp.java
 * Description : Test class for the LevelUp class, which handles the level-up process of a Bugemon
 * @author Gouverneur Martin
 * @date 09 mar. 2026
 * @version 1.0
 */

package ulb.models.level_up;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.services.LevelUpService;
import ulb.utils.test.TestUtilsBugemons;

public class TestLevelUp {
    private Bugemon bugemon;

    @Test
    public void testChoicesTotalTenPoints() {
        bugemon = TestUtilsBugemons.createDefaultBugemon("1");

        LevelUp levelUp = new LevelUp(bugemon);
        List<Choice> choices = levelUp.getChoices();

        for (Choice choice : choices) {
            // 1 point is worth 2 HP or 1 Initiative, while Attack and Defense are worth 1 point
            // each
            int pointsHp = choice.getBonusHP() / 2;
            int pointsAttack = choice.getBonusAttack();
            int pointsDefense = choice.getBonusDefense();
            int pointsInitiative = choice.getBonusInitiative() / 2;

            int totalPoints = pointsHp + pointsAttack + pointsDefense + pointsInitiative;

            assertEquals(10, totalPoints);

            assertTrue(choice.getBonusHP() % 2 == 0);
            assertTrue(choice.getBonusInitiative() % 2 == 0);
            assertTrue(choice.getBonusHP() >= 0 && choice.getBonusAttack() >= 0
                       && choice.getBonusDefense() >= 0 && choice.getBonusInitiative() >= 0);
        }
    }

    @Test
    public void testBugemonExperienceAndLevelProperties() {
        bugemon = TestUtilsBugemons.createDefaultBugemon("1");

        assertEquals(1, bugemon.getLevel());
        assertEquals(0, bugemon.getXp());

        bugemon.gainXp(50);
        List<LevelUp> levelUps = LevelUpService.levelUp(List.of(bugemon));
        assertEquals(1, levelUps.size());

        assertEquals(50, bugemon.getXp());
        assertEquals(2, bugemon.getLevel());
    }
}
