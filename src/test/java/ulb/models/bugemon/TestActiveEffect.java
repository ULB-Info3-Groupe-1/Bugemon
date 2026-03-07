/**
 * File name : TestActiveEffect.java
 * Description : Test class for the ActiveEffect class.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestActiveEffect {

    @Test
    public void testGetEffect() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                "TestEffect",
                EffectStat.DEFENSE,
                10,
                "1_tour");
        int duration = effect.extractDuration();
        ActiveEffect activeEffect = new ActiveEffect(effect, duration);
        assertEquals(activeEffect.getEffect(), effect);
    }

    @Test
    public void testDecrement() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                "TestEffect",
                EffectStat.DEFENSE,
                10,
                "1_tour");
        int duration = effect.extractDuration();
        ActiveEffect activeEffect = new ActiveEffect(effect, duration);
        assertEquals(activeEffect.getDuration(), duration);
        activeEffect.decrementDuration();
        assertEquals(activeEffect.getDuration(), duration - 1);
        assertTrue(activeEffect.isExpired());
    }
}
