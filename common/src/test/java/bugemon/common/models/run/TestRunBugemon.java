package bugemon.common.models.run;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.models.BugemonFixtures;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.player.BonusStats;
import bugemon.common.models.player.PlayerBugemon;

public class TestRunBugemon {

    private RunBugemon runBugemon;

    @Before
    public void setUp() {
        Attack atk1 = BugemonFixtures.attack("a1", 40);
        Attack atk2 = BugemonFixtures.attack("a2", 50);
        Attack atk3 = BugemonFixtures.attack("a3", 30);

        Bugemon base = new Bugemon("florachu", 90, 55, 40, 50, ElementType.FLORA, List.of(atk1, atk2, atk3), "", false,
                false);
        this.runBugemon = new RunBugemon(new PlayerBugemon(base));
    }

    @Test
    public void shouldStartAtFullHpAndLevelOne() {
        assertEquals(1, this.runBugemon.getLevel());
        assertEquals(90, this.runBugemon.getMaxHp());
        assertEquals(90, this.runBugemon.getCurrentHp());
        assertEquals(55, this.runBugemon.getAttack());
        assertEquals(40, this.runBugemon.getDefense());
        assertEquals(50, this.runBugemon.getInitiative());
        assertEquals(0.0, this.runBugemon.getXpProgress(), 0.0001);
    }

    @Test
    public void addXpShouldLevelUpAndKeepRemainderXp() {
        int levelsGained = this.runBugemon.addXp(70);

        assertEquals(1, levelsGained);
        assertEquals(2, this.runBugemon.getLevel());
        assertEquals(20, this.runBugemon.getPlayerBugemon().getXp());
    }

    @Test
    public void applyBonusShouldRestoreHpToNewMax() {
        this.runBugemon.setCurrentHp(60);

        this.runBugemon.applyBonus(new BonusStats(20, 10, 5, 0));

        assertEquals(110, this.runBugemon.getMaxHp());
        assertEquals(110, this.runBugemon.getCurrentHp());
        assertEquals(65, this.runBugemon.getAttack());
        assertEquals(45, this.runBugemon.getDefense());
        assertEquals(50, this.runBugemon.getInitiative());
    }

    @Test
    public void restoreHpToMaxShouldSetCurrentHpToMax() {
        this.runBugemon.setCurrentHp(10);
        this.runBugemon.restoreHpToMax();
        assertEquals(this.runBugemon.getMaxHp(), this.runBugemon.getCurrentHp());
    }

    @Test
    public void getAttacksShouldBeUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.runBugemon.getAttacks().add(BugemonFixtures.attack("new", 1));
        });
    }

    @Test
    public void constructorShouldRejectNegativeHp() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RunBugemon(this.runBugemon.getPlayerBugemon(), -1);
        });
    }

    @Test
    public void isKoShouldBeTrueWhenHpIsZero() {
        assertFalse(this.runBugemon.isKo());
        this.runBugemon.setCurrentHp(0);
        assertTrue(this.runBugemon.isKo());
    }

    @Test
    public void canLearnShouldReturnFalseWhenAttackIsSuperEffective() {
        // AQUA bugemon cannot learn a FLORA attack (FLORA is super-effective against
        // AQUA)
        RunBugemon aquaBugemon = new RunBugemon(new PlayerBugemon(BugemonFixtures.slowAqua()));
        assertFalse(aquaBugemon.canLearn(BugemonFixtures.floraAttack()));
    }

    @Test
    public void canLearnShouldReturnTrueWhenAttackIsNotSuperEffective() {
        // AQUA bugemon can learn an AQUA attack (neutral)
        RunBugemon aquaBugemon = new RunBugemon(new PlayerBugemon(BugemonFixtures.slowAqua()));
        assertTrue(aquaBugemon.canLearn(BugemonFixtures.aquaAttack()));
    }

    @Test
    public void canLearnShouldReturnTrueForNormalTypeAttack() {
        // NORMAL type is never super-effective
        assertTrue(this.runBugemon.canLearn(BugemonFixtures.attack("normal-atk", 30)));
    }
}
