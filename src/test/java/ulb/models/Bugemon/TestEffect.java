/**
 * Nom du fichier : TestEffect.java
 * Description : Test class for the Effect class.
 * 
 * @author Liefferinckx Romain
 * @date 24 févr. 2026
 * @version 1.0
 */

package ulb.models.Bugemon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestEffect {
    @Test
    public void testCreationEffect() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        assertTrue(effect.getType().equals("TestEffect"));
        assertTrue(effect.getTarget().equals("TestEffect"));
        assertTrue(effect.getStat().equals("Flora"));
        assertTrue(effect.getDuration().equals("1 turn"));
        assertTrue(effect.getModifier() == 10);
    }

    @Test
    public void testGettersAndSetters() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        effect.setType("NewTestEffect");
        effect.setTarget("NewTestEffect");
        effect.setStat("Aqua");
        effect.setDuration("2 turns");
        effect.setModifier(20);
        assertTrue(effect.getType().equals("NewTestEffect"));
        assertTrue(effect.getTarget().equals("NewTestEffect"));
        assertTrue(effect.getStat().equals("Aqua"));
        assertTrue(effect.getDuration().equals("2 turns"));
        assertTrue(effect.getModifier() == 20);
    }
}
