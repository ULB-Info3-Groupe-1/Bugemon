package ulb.models.trainer;

import ulb.models.utils.Vec2;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTrainerWalk {
    @Test
    public void testProgressAtHalfDuration() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        trainerWalk.moveTo(new Vec2(10, 0));
        trainerWalk.update(0.25f); // moitié du temps (0.5s / 2)
        Vec2 pos = trainerWalk.getPosition();
        assertEquals(5f, pos.x, 0.001);
        assertEquals(0f, pos.y, 0.001);
        assertTrue(trainerWalk.isMoving());
    }

    @Test
    public void testArrival() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        trainerWalk.moveTo(new Vec2(10, 0));
        trainerWalk.update(0.5f); // durée complète
        Vec2 pos = trainerWalk.getPosition();
        assertEquals(10f, pos.x, 0.001);
        assertEquals(0f, pos.y, 0.001);
        assertFalse(trainerWalk.isMoving());
    }

    @Test
    public void testMultipleUpdatesAndTotalTime() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        trainerWalk.moveTo(new Vec2(10, 0));

        float deltaTime = 0.1f;
        float totalTime = 0f;
        while (trainerWalk.isMoving()) {
            trainerWalk.update(deltaTime);
            totalTime += deltaTime;
            Vec2 pos = trainerWalk.getPosition();
            assertTrue(pos.x >= 0 && pos.x <= 10);
        }
        assertEquals(0.5f, totalTime, 0.001);
    }

    @Test
    public void testMoveWhileMoving() {
        TrainerWalk trainerWalk = new TrainerWalk(new Vec2(0, 0));
        trainerWalk.moveTo(new Vec2(10, 0));
        trainerWalk.update(0.1f);
        trainerWalk.moveTo(new Vec2(5, 0)); // tentative de changer la cible
        Vec2 pos = trainerWalk.getPosition();
        assertEquals(0.1f / 0.5f * 10, pos.x, 0.001); // continue vers 10, pas 5
    }
}
