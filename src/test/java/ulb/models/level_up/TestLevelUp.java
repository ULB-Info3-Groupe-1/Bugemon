package ulb.models.level_up;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.models.bugemon.Bugemon;
import ulb.utils.TestUtilsBugemons;


public class TestLevelUp {

    private Bugemon bugemon;

    @BeforeEach
    public void setUp() {
        bugemon = TestUtilsBugemons.createDefaultBugemon("1");
    }

    @Test
    public void testLevelUpRestoresMaxHP() {
        bugemon.takeDamage(40);
        assertEquals(60, bugemon.getHp());

        bugemon.levelUp();

        // Bugemon health should be restored to full
        assertEquals(100, bugemon.getHp());
    }

    @Test
    public void testLevelUpGeneratesThreeChoices() {
        LevelUp levelUp = bugemon.levelUp();
        List<Choice> choices = levelUp.getChoices();

        assertNotNull(choices);
        assertEquals(3, choices.size());
    }

    @Test
    public void testChoicesTotalTenPoints() {
        LevelUp levelUp = new LevelUp(bugemon);
        List<Choice> choices = levelUp.getChoices();

        for (Choice choice : choices) {
            // 1 point is worth 2 HP or 1 Initiative, while Attack and Defense are worth 1 point each
            int pointsHp = choice.getBonusHP() / 2;
            int pointsAttack = choice.getBonusAttack();
            int pointsDefense = choice.getBonusDefense();
            int pointsInitiative = choice.getBonusInitiative() / 2;
            
            int totalPoints = pointsHp + pointsAttack + pointsDefense + pointsInitiative;
            
            assertEquals(10, totalPoints);
            
            assertTrue(choice.getBonusHP() % 2 == 0);
            assertTrue(choice.getBonusInitiative() % 2 == 0);
            assertTrue(choice.getBonusHP() >= 0 && choice.getBonusAttack() >= 0 && choice.getBonusDefense() >= 0 && choice.getBonusInitiative() >= 0);
        }
    }

    @Test
    public void testBugemonExperienceAndLevelProperties() {
        assertEquals(1, bugemon.getLevel());
        assertEquals(0, bugemon.getXp());

        bugemon.addXp(50);
        assertEquals(50, bugemon.getXp());
        assertEquals(2, bugemon.getLevel());

        bugemon.addXp(150);
        assertEquals(150, bugemon.getXp());
        assertEquals(3, bugemon.getLevel());
    }
}

