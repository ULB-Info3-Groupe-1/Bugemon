/**
 * File name : TestAttack.java
 * Description : Test class for the Attack class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestAttack {
    @Test
    public void testShouldGetIdAndSetId() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        attack.setId("NewTestAttack");
        assertTrue(attack.getId().equals("NewTestAttack"));
    }

    @Test
    public void testShouldGetNameAndSetName() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);

        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        attack.setName("NewTestAttack");
        assertTrue(attack.getName().equals("NewTestAttack"));
    }

    @Test
    public void testShouldGetTypeAndSetType() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);

        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        attack.setType(Bugemon.Type.AQUA);
        assertTrue(attack.getType().equals(Bugemon.Type.AQUA));
    }

    @Test
    public void testShouldGetDescriptionAndSetDescription() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        attack.setDescription("New description");
        assertTrue(attack.getDescription().equals("New description"));
    }

    @Test
    public void testShouldGetPowerAndSetPower() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        attack.setPower(40);
        assertTrue(attack.getPower() == 40);
    }

    @Test
    public void testShouldGetEffectAndSetEffect() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack("TestAttack", "TestAttack", Bugemon.Type.FLORA, "", 30, effects);
        Effect newEffect = new Effect("NewTypeEffect", "NewTestEffect", "Aqua", 20, "2 turns");
        attack.addEffect(newEffect);
        assertTrue(attack.getEffects().contains(newEffect));
    }

}
