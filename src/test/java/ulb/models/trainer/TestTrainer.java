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

import java.util.Map;
import org.junit.Test;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.TestUtilsBugemonTeam;

public class TestTrainer {

    public static void killBugemon(BugemonTeam team, String id) {
        team.getBugemon(id).takeDamage(team.getBugemon(id).getStats().getHp());
    }

    @Test
    public void testTeamStatus() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        killBugemon(team, "2");
        Trainer trainer = new Trainer(team);
        Map<Bugemon, Boolean> expectedStatus = trainer.teamStatus();
        assertFalse(expectedStatus.get(team.getBugemon("2")));
    }

    @Test
    public void testIsDefeated() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new Trainer(team);
        assertTrue(trainer.isDefeated());
    }
}
