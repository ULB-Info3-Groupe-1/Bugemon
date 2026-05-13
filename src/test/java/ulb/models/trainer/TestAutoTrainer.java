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

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.Team;
import ulb.utils.test.TestUtilsBugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestAutoTrainer {
    @Test
    public void testSelectRandomBugemon() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        AutoTrainer trainer = new AutoTrainer(team);
        TestUtilsBugemons.killBugemon(team, "1");
        trainer.selectRandomBugemon();
        assertTrue(trainer.isCurrentBugemonAlive());
        Bugemon killed = team.stream().filter(b -> b.getName().equals("1")).findFirst().get();
        assertNotEquals(killed, trainer.getCurrentBugemon());
    }
}
