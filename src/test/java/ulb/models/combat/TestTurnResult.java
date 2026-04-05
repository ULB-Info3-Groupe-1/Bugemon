package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import ulb.models.trainer.AutoTrainer;
import ulb.utils.test.TestUtilsTrainer;

public class TestTurnResult {

    @Test
    public void steps_onEmptyResult_returnsEmptyIterator() {
        TurnResult result = new TurnResult();
        assertFalse(result.steps().hasNext());
    }

    @Test
    public void addStep_singleStep_iteratorContainsExactlyThatStep() {
        AutoTrainer trainer = TestUtilsTrainer.createDefaultAutoTrainer();
        TurnResult result = new TurnResult();
        TurnStep step = new TurnStep.BugemonKoStep(trainer);
        result.addStep(step);

        Iterator<TurnStep> it = result.steps();
        assertTrue(it.hasNext());
        assertEquals(step, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void addStep_multipleSteps_iteratesInInsertionOrder() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        TurnResult result = new TurnResult();

        TurnStep step1 = new TurnStep.BugemonKoStep(t1);
        TurnStep step2 = new TurnStep.TrainerKoStep(t2);
        TurnStep step3 = new TurnStep.ForfeitStep(t1);
        result.addStep(step1);
        result.addStep(step2);
        result.addStep(step3);

        Iterator<TurnStep> it = result.steps();
        assertEquals(step1, it.next());
        assertEquals(step2, it.next());
        assertEquals(step3, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void addStep_afterIteratorExhausted_newCallToStepsReturnsAll() {
        AutoTrainer trainer = TestUtilsTrainer.createDefaultAutoTrainer();
        TurnResult result = new TurnResult();
        result.addStep(new TurnStep.ForfeitStep(trainer));

        // consume first iterator
        result.steps().forEachRemaining(s -> {
        });

        // second call should still return all steps
        assertTrue(result.steps().hasNext());
    }
}
