/**
 * File name : TestEffect.java
 * Description : Test class for the Effect class.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestEffect {

    @Test
    public void testShouldGetTypeEffectAndSetEffectType() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        assertTrue(effect.getTypeEffect().equals(EffectType.STAT_MODIFIER));
        effect.setTypeEffect(EffectType.SOIN);
        assertTrue(effect.getTypeEffect().equals(EffectType.SOIN));
    }

    @Test
    public void testShouldGetTargetAndSetTarget() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        assertTrue(effect.getTarget().equals("TestEffect"));
        effect.setTarget("NewTestEffect");
        assertTrue(effect.getTarget().equals("NewTestEffect"));
    }

    @Test
    public void testShouldGetStatAndSetStat() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        assertTrue(effect.getStat().equals("Flora"));
        effect.setStat("Aqua");
        assertTrue(effect.getStat().equals("Aqua"));
    }

    @Test
    public void testShouldGetDurationAndSetDuration() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        assertTrue(effect.getDuration().equals("1 turn"));
        effect.setDuration("2 turns");
        assertTrue(effect.getDuration().equals("2 turns"));
    }

    @Test
    public void testShouldGetModifierAndSetModifier() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        assertTrue(effect.getModifier() == 10);
        effect.setModifier(20);
        assertTrue(effect.getModifier() == 20);
    }
}
