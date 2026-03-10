/**
 * File name : TestAutoTrainer.java
 * Description : Test class for the AutoTrainer class.
 *
 * @author Liefferinckx Romain
 * @date 01 march. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.test.TestUtilsBugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestAutoTrainer {
    @Test
    public void testSelectRandomBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        AutoTrainer trainer = new AutoTrainer(team);
        TestUtilsBugemons.killBugemon(team, "1");
        trainer.selectRandomBugemon();
        assertTrue(trainer.isCurrentBugemonAlive());
        assertNotEquals(team.getBugemon("1"), trainer.getCurrentBugemon());
    }
}
