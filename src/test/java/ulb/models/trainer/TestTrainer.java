package ulb.models.trainer;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.utils.test.TestUtilsBugemonTeam;

public class TestTrainer {
    @Test
    public void testIsDefeated() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(true);
        Trainer trainer = new AutoTrainer(team);
        assertTrue(trainer.isDefeated());
    }
}
