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

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Effect;

public class TestAttack {
    @Test
    public void testShouldGetIdAndSetId() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        attack.setId("NewTestAttack");
        assertTrue(attack.getId().equals("NewTestAttack"));
    }

    @Test
    public void testShouldGetNameAndSetName() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        attack.setName("NewTestAttack");
        assertTrue(attack.getName().equals("NewTestAttack"));
    }

    @Test
    public void testShouldGetTypeAndSetType() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        attack.setType(ulb.models.bugemon.Type.AQUA);
        assertTrue(attack.getType().equals(ulb.models.bugemon.Type.AQUA));
    }

    @Test
    public void testShouldGetDescriptionAndSetDescription() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        attack.setDescription("New description");
        assertTrue(attack.getDescription().equals("New description"));
    }

    @Test
    public void testShouldGetPowerAndSetPower() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        attack.setPower(40);
        assertTrue(attack.getPower() == 40);
    }

    @Test
    public void testShouldGetEffectAndSetEffect() {
        Effect effect = new Effect(ulb.models.bugemon.Type.FLORA, "TestEffect", "Flora", 10, "1 turn");
        Attack attack = new Attack("TestAttack", "TestAttack", ulb.models.bugemon.Type.FLORA, "", 30, effect);
        Effect newEffect = new Effect(ulb.models.bugemon.Type.AQUA, "NewTestEffect", "Aqua", 20, "2 turns");
        attack.setEffect(newEffect);
        assertTrue(attack.getEffect() == newEffect);
    }

}
