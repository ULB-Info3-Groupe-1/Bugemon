package ulb.models.trainer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon_team.Team;
import ulb.utils.test.TestUtilsBugemonTeam;

public class TestTrainer {
    @Test
    public void testIsDefeated() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new AutoTrainer(team);
        assertTrue(trainer.isDefeated());
    }
}
