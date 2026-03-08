/**
 * File name : TestEffect.java
 * Description : Test class for the Effect class.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestEffect {

    @Test
    public void testShouldGetTypeEffectAndSetEffectType() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                EffectTarget.ADVERSARY,
                EffectStat.DEFENSE,
                10,
                "1 turn");
        assertEquals(EffectType.STAT_MODIFIER, effect.getTypeEffect());
        effect.setTypeEffect(EffectType.SOIN);
        assertEquals(EffectType.SOIN, effect.getTypeEffect());
    }

    @Test
    public void testShouldGetTargetAndSetTarget() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                EffectTarget.ADVERSARY,
                EffectStat.DEFENSE,
                10,
                "1 turn");
        assertEquals(EffectTarget.ADVERSARY, effect.getTarget());
        effect.setTarget(EffectTarget.THROWER);
        assertEquals(EffectTarget.THROWER, effect.getTarget());
    }

    @Test
    public void testShouldGetStatAndSetStat() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                EffectTarget.ADVERSARY,
                EffectStat.DEFENSE,
                10,
                "1 turn");
        assertEquals(EffectStat.DEFENSE, effect.getStat());
        effect.setStat(EffectStat.ATTACK);
        assertEquals(EffectStat.ATTACK, effect.getStat());
    }

    @Test
    public void testShouldGetDurationAndSetDuration() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                EffectTarget.ADVERSARY,
                EffectStat.DEFENSE,
                10,
                "1 turn");
        assertEquals("1 turn", effect.getDuration());
        effect.setDuration("2 turns");
        assertEquals("2 turns", effect.getDuration());
    }

    @Test
    public void testShouldGetModifierAndSetModifier() {
        Effect effect = new Effect(
                EffectType.STAT_MODIFIER,
                EffectTarget.ADVERSARY,
                EffectStat.DEFENSE,
                10,
                "1 turn");
        assertEquals(10, effect.getModifier());
        effect.setModifier(20);
        assertEquals(20, effect.getModifier());
    }
}
