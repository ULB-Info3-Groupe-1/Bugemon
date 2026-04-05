package ulb.models.combat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.models.trainer.AutoTrainer;
import ulb.utils.test.TestUtilsTrainer;

public class TestAutomaticCombat {

    private static List<TurnStep> stepsAsList(TurnResult result) {
        List<TurnStep> list = new ArrayList<>();
        result.steps().forEachRemaining(list::add);
        return list;
    }

    private static boolean hasTrainerKoStep(TurnResult result) {
        return stepsAsList(result).stream().anyMatch(s -> s instanceof TurnStep.TrainerKoStep);
    }

    private static void processKoReactions(TurnResult result) {
        stepsAsList(result).forEach(step -> {
            if (step instanceof TurnStep.BugemonKoStep koStep && !koStep.trainer().isDefeated()) {
                koStep.trainer().reactToKo();
            }
        });
    }

    @Test
    public void testBasicTurn() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(trainer1, trainer2);
        combat.turn();
        assertFalse(combat.getPlayerTrainer().isDefeated());
        assertFalse(combat.getOpponentTrainer().isDefeated());
    }

    @Test
    public void testEndingTurn() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(trainer1, trainer2);

        TurnResult lastResult = null;
        boolean ended = false;
        int maxTurns = 1000;

        while (!ended && maxTurns-- > 0) {
            lastResult = combat.turn();
            processKoReactions(lastResult);
            ended = hasTrainerKoStep(lastResult);
        }

        assertTrue(ended);
        assertTrue(trainer1.isDefeated() || trainer2.isDefeated());
    }

    @Test
    public void testApplyDamage() {
        AutoTrainer trainer1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer trainer2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(trainer1, trainer2);
        int initialHp1 = trainer1.getCurrentBugemonHp();
        int initialHp2 = trainer2.getCurrentBugemonHp();
        combat.turn();
        int finalHp1 = trainer1.getCurrentBugemonHp();
        int finalHp2 = trainer2.getCurrentBugemonHp();
        assertTrue(finalHp1 < initialHp1);
        assertTrue(finalHp2 < initialHp2);
    }
}
