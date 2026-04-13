package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.utils.Vec2;

public class TestTrainerWalk {
    @Test
    public void testMoveToIfWalkableAcceptsWalkablePath() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        int[][] matrix = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

        boolean started = trainerWalk.moveToIfWalkable(new Vec2(2, 0), matrix);

        assertTrue(started);
        assertTrue(trainerWalk.isMoving());
    }

    @Test
    public void testMoveToIfWalkableRejectsBlockedTarget() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        int[][] matrix = {{1, 1, 0}, {1, 1, 1}, {1, 1, 1}};

        boolean started = trainerWalk.moveToIfWalkable(new Vec2(2, 0), matrix);

        assertFalse(started);
        assertFalse(trainerWalk.isMoving());
        assertEquals(0f, trainerWalk.getPosition().x(), 0.001);
        assertEquals(0f, trainerWalk.getPosition().y(), 0.001);
    }

    @Test
    public void testMoveToIfWalkableRejectsBlockedIntermediateCell() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        int[][] matrix = {{1, 0, 1}, {1, 1, 1}, {1, 1, 1}};

        boolean started = trainerWalk.moveToIfWalkable(new Vec2(2, 0), matrix);

        assertTrue(started);
        while (trainerWalk.isMoving()) {
            trainerWalk.update(0.1f);
        }

        assertEquals(2f, trainerWalk.getPosition().x(), 0.001);
        assertEquals(0f, trainerWalk.getPosition().y(), 0.001);
    }

    @Test
    public void testMoveToIfWalkableRejectsWhenNoPathExists() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        int[][] matrix = {{1, 0, 1}, {0, 0, 0}, {1, 1, 1}};

        boolean started = trainerWalk.moveToIfWalkable(new Vec2(2, 0), matrix);

        assertFalse(started);
        assertFalse(trainerWalk.isMoving());
    }
}
