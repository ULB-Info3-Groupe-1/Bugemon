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

import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;

public class TestEffect {

    @Test
    public void testShouldGetTargetAndSetTarget() {
        EffectStatModifier effect = new EffectStatModifier(EffectTarget.ADVERSARY, 
                                                           EffectStat.DEFENSE, 
                                                           10, 
                                                           "1 turn");
        assertEquals(EffectTarget.ADVERSARY, effect.target());
    }
    @Test
    public void testShouldGetStatAndSetStat() {
        EffectStatModifier effect = new EffectStatModifier(EffectTarget.ADVERSARY, 
                                                           EffectStat.DEFENSE, 
                                                           10, 
                                                           "1 turn");
        assertEquals(EffectStat.DEFENSE, effect.stat());
    }

    @Test
    public void testShouldGetDurationAndSetDuration() {
        EffectStatModifier effect = new EffectStatModifier(EffectTarget.ADVERSARY, 
                                                           EffectStat.DEFENSE, 
                                                           10, 
                                                           "1 turn");
        assertEquals("1 turn", effect.duration());
    }

    @Test
    public void testShouldGetModifierAndSetModifier() {
        EffectStatModifier effect = new EffectStatModifier(EffectTarget.ADVERSARY, 
                                                            EffectStat.DEFENSE, 
                                                            10, 
                                                            "1 turn");
        assertEquals(10, effect.modifier());
    }
}
