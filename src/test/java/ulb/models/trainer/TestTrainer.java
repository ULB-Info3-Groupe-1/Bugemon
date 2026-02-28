/**
 * File name : TestTrainer.java
 * Description : Test class for the Trainer class.
 * 
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import java.util.Map;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.Trainer;
import ulb.utils.TestUtilsBugemonTeam;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTrainer {

    private void killBugemon(BugemonTeam team, int index) {
        team.getBugemon(index).takeDamage(team.getBugemon(index).getStats().getHp());
    }

    @Test
    public void testTeamStatus() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        killBugemon(team, 2);
        Trainer trainer = new Trainer(team);
        Map<Bugemon, Boolean> expectedStatus = trainer.teamStatus();
        assertFalse(expectedStatus.get(team.getBugemon(2)));
    }

    @Test
    public void testIsDefeated() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new Trainer(team);
        assertTrue(trainer.isDefeated());
    }
}
