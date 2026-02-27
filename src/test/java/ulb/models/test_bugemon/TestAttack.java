/**
 * File name : TestAttack.java
 * Description : Test class for the Attack class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.test_bugemon;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Effect;

public class TestAttack {
    @Test
    public void testCreationAttack() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = List.of(effect);
        Attack attack = new Attack("TestAttack", "TestAttack", "Flora", "", 30, effects);
        assertTrue(attack.getId().equals("TestAttack"));
        assertTrue(attack.getName().equals("TestAttack"));
        assertTrue(attack.getType().equals("Flora"));
        assertTrue(attack.getDescription().equals(""));
        assertTrue(attack.getPower() == 30);
        assertTrue(attack.getEffects().get(0) == effect);
    }

    @Test
    public void testGettersAndSetters() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>(List.of(effect));
        Attack attack = new Attack("TestAttack", "TestAttack", "Flora", "", 30, effects);
        attack.setId("NewTestAttack");
        attack.setName("NewTestAttack");
        attack.setType("Aqua");
        attack.setDescription("New description");
        attack.setPower(40);
        Effect newEffect = new Effect("NewTestEffect", "NewTestEffect", "Aqua", 20, "2 turns");
        attack.addEffect(newEffect);
        assertTrue(attack.getId().equals("NewTestAttack"));
        assertTrue(attack.getName().equals("NewTestAttack"));
        assertTrue(attack.getType().equals("Aqua"));
        assertTrue(attack.getDescription().equals("New description"));
        assertTrue(attack.getPower() == 40);
        assertTrue(attack.getEffects().get(1) == newEffect);
    }
}
