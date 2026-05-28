package bugemon.common.models.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.player.exceptions.IllegalAttackReplacementException;

public class TestPlayerBugemon {

    private static Attack normalAttack(String id) {
        return new Attack(id, "Coup Normal", "", 30, ElementType.NORMAL, List.of());
    }

    private static Attack floraAttack(String id) {
        return new Attack(id, "Fouet-Liane", "", 30, ElementType.FLORA, List.of());
    }

    private PlayerBugemon playerBugemon;
    private Attack baseAttack;

    @Before
    public void setUp() {
        this.baseAttack = normalAttack("atk-base");
        Bugemon base = new Bugemon("FloraTest", 100, 50, 30, 40, ElementType.FLORA,
                List.of(this.baseAttack, this.baseAttack, this.baseAttack), "", false, false);
        this.playerBugemon = new PlayerBugemon(base);
    }

    @Test
    public void shouldThrowIllegalAttackReplacement_whenBugemonIsWeakAgainstAttackType() {
        Attack aqua = new Attack("atk-aqua", "Jet d'Eau", "", 30, ElementType.AQUA, List.of());
        assertThrows(IllegalAttackReplacementException.class, () -> {
            this.playerBugemon.replaceAttack(this.baseAttack, aqua);
        });
    }

    @Test
    public void shouldThrowIllegalAttackReplacement_whenOldAttackNotInMoveSet()
            throws IllegalAttackReplacementException {
        Attack notOwned = normalAttack("atk-not-owned");
        Attack replacement = normalAttack("atk-replacement");
        assertThrows(IllegalAttackReplacementException.class, () -> {
            this.playerBugemon.replaceAttack(notOwned, replacement);
        });
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenAddXpCalledWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.playerBugemon.addXp(-1);
        });
    }

    @Test
    public void shouldNotLevelUp_whenXpBelowThreshold() {
        int initialLevel = this.playerBugemon.getLevel();
        this.playerBugemon.addXp(10);
        assertEquals(initialLevel, this.playerBugemon.getLevel());
        assertEquals(10, this.playerBugemon.getXp());
    }

    @Test
    public void shouldLevelUp_whenXpReachesThreshold() {
        // At level 1, threshold is 50 + 50*(1-1) = 50
        int levelsGained = this.playerBugemon.addXp(50);
        assertEquals(1, levelsGained);
        assertEquals(2, this.playerBugemon.getLevel());
        assertEquals(0, this.playerBugemon.getXp());
    }

    @Test
    public void shouldLevelUpMultipleTimes_whenXpExceedsMultipleThresholds() {
        int levelsGained = this.playerBugemon.addXp(150);
        assertEquals(2, levelsGained);
        assertEquals(3, this.playerBugemon.getLevel());
        assertEquals(0, this.playerBugemon.getXp());
    }

    @Test
    public void shouldReplaceAttack_whenNewAttackIsCompatible() throws IllegalAttackReplacementException {
        Attack floraAtk = floraAttack("atk-flora");
        boolean result = this.playerBugemon.replaceAttack(this.baseAttack, floraAtk);
        assertEquals(true, result);
    }
}
