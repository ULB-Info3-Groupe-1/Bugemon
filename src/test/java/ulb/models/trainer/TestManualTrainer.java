/**
 * File name : TestManualTrainer.java
 * Description : Test class for the ManualTrainer class.
 *
 * @author Liefferinckx Romain
 * @date 02 march. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertEquals;

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
        assertEquals(team.getBugemon("2"), trainer.getSelectedBugemon());
    }

    @Test
    public void testSelectBugemonWhenDead() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestTrainer.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);
        assertThrows(IllegalArgumentException.class, () -> trainer.selectBugemon(team.getBugemon("2")));
    }
}
