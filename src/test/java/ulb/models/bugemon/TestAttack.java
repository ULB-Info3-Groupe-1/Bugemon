/**
 * File name : TestAttack.java
 * Description : Test class for the Attack class.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TestAttack {

    @Test
    public void testShouldGetIdAndSetId() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        attack.setId("NewTestAttack");
        assertEquals("NewTestAttack", attack.getId());
    }

    @Test
    public void testShouldGetNameAndSetName() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);

        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        attack.setName("NewTestAttack");
        assertEquals("NewTestAttack", attack.getName());
    }

    @Test
    public void testShouldGetTypeAndSetType() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);

        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        attack.setType(Bugemon.BType.AQUA);
        assertEquals(Bugemon.BType.AQUA, attack.getType());
    }

    @Test
    public void testShouldGetDescriptionAndSetDescription() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        attack.setDescription("New description");
        assertEquals("New description", attack.getDescription());
    }

    @Test
    public void testShouldGetPowerAndSetPower() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        attack.setPower(40);
        assertEquals(40, attack.getPower());
    }

    @Test
    public void testShouldGetEffectAndSetEffect() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack = new Attack(
            "TestAttack",
            "TestAttack",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Effect newEffect = new Effect(
            EffectType.STAT_MODIFIER,
            "NewTestEffect",
            "Aqua",
            20,
            "2 turns"
        );
        attack.addEffect(newEffect);
        assertTrue(attack.containsEffect(newEffect));
    }
}
