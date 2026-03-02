/**
 * File name : TestManualTrainer.java
 * Description : Test class for the ManualTrainer class.
 *
 * @author Liefferinckx Romain
 * @date 02 march. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.TestUtilsBugemonTeam;

public class TestManualTrainer {

    public static void killBugemon(BugemonTeam team, String id) {
        team.getBugemon(id).takeDamage(team.getBugemon(id).getStats().getHp());
    }

    @Test
    public void testSelectBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        trainer.selectBugemon(team.getBugemon("2"));
        assertTrue(trainer.getSelectedBugemon().equals(team.getBugemon("2")));
    }

    @Test
    public void testSelectBugemonWhenDead() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestTrainer.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);
        trainer.selectBugemon(team.getBugemon("2"));
        assertNull(trainer.getSelectedBugemon());
    }


}
