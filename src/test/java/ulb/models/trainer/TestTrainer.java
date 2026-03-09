/**
 * File name : TestTrainer.java
 * Description : Test class for the Trainer class.
 *
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import static org.junit.Assert.*;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.TestUtilsBugemonTeam;

public class TestTrainer {
    @Test
    public void testIsDefeated() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new Trainer(team);
        assertTrue(trainer.isDefeated());
    }
}
