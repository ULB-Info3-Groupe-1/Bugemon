/**
 * File name : TestEffect.java
 * Description : Test class for the Effect class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.test_bugemon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon.Effect;

public class TestEffect {
    @Test
    public void testShouldGetTypeAndSetType() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setType("NewTestEffect");
        assertTrue(effect.getType().equals("NewTestEffect"));
    }

    @Test
    public void testShouldGetTargerAndSetTarger() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setTarget("NewTestEffect");
        assertTrue(effect.getTarget().equals("NewTestEffect"));
    }

    @Test
    public void testShouldGetStatAndSetStat() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setStat("Aqua");
        assertTrue(effect.getStat().equals("Aqua"));
    }

    @Test
    public void testShouldGetDurationAndSetDuration() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setDuration("2 turns");
        assertTrue(effect.getDuration().equals("2 turns"));
    }

    @Test
    public void testShouldGetModifierAndSetModifier() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setModifier(20);
        assertTrue(effect.getModifier() == 20);
    }
}
