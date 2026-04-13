package ulb.models.trainer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.test.TestUtilsBugemonTeam;

public class TestTrainer {
    @Test
    public void testIsDefeated() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new AutoTrainer(team);
        assertTrue(trainer.isDefeated());
    }
}
