/**
 * File name : TestTrainer.java
 * Description : Test class for the Trainer class.
 * 
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.test_trainer;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestTrainer {
    @Test
    public void testTrainerCreation() {
        assertNotNull(ulb.utils.TestUtilsTrainer.createDefaultTrainer());
    }

    @Test
    public void testIsDefeated() {
        ulb.models.trainer.Trainer trainer = ulb.utils.TestUtilsTrainer.createDefaultTrainer();
        assertFalse(trainer.isDefeated());
        for (int i = 0; i < 6; i++) {
            trainer.decreaseBugemonAlive();
        }
        assertTrue(trainer.isDefeated());
    }
}
