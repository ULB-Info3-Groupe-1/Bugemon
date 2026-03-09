/**
 * File name : TestManualTrainer.java
 * Description : Test class for the ManualTrainer class.
 *
 * @author Liefferinckx Romain
 * @date 02 march. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.TestUtilsBugemonTeam;
import ulb.utils.TestUtilsBugemons;

public class TestManualTrainer {
    @Test
    public void testSelectBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon bugemon = (team.getBugemon("2").get());
        trainer.selectBugemon(bugemon);
        assertEquals(bugemon, trainer.getSelectedBugemon());
    }

    @Test
    public void testSelectBugemonWhenDead() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);
        assertThrows(IllegalArgumentException.class,
                     () -> trainer.selectBugemon(team.getBugemon("2").get()));
    }
}
