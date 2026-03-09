/**
 * File name : TestAutomaticCombat.java
 * Description : Test class for the AutomaticCombat class.
 *
 * @author Liefferinckx Romain
 * @date 01 march. 2026
 * @version 1.0
 */

package ulb.models.combat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.trainer.AutoTrainer;
import ulb.utils.TestUtilsTrainer;

public class TestAutomaticCombat {
    @Test
    public void testBasicTurn() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutomaticCombat combat = new AutomaticCombat(trainer1, trainer2);
        combat.turn();
        assertFalse(combat.getAllyTrainer().isDefeated());
        assertFalse(combat.getAdversaryTrainer().isDefeated());
    }

    @Test
    public void testEndingTurn() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutomaticCombat combat = new AutomaticCombat(trainer1, trainer2);
        while (!combat.isFinished()) {
            combat.turn();
        }
        assertTrue(combat.isFinished());
        assertNotNull(combat.getWinner());
    }

    @Test
    public void testApplyDamage() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutomaticCombat combat = new AutomaticCombat(trainer1, trainer2);
        int initialHp1 = trainer1.getCurrentBugemonHp();
        int initialHp2 = trainer2.getCurrentBugemonHp();
        combat.turn();
        int finalHp1 = trainer1.getCurrentBugemonHp();
        int finalHp2 = trainer2.getCurrentBugemonHp();
        assertTrue(finalHp1 < initialHp1);
        assertTrue(finalHp2 < initialHp2);
    }

    // TODO: Need to implement a test on the persistency of the permanent effect
}
