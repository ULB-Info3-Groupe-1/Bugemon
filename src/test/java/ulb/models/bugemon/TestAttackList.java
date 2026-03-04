/**
 * File name : TestAttackList.java
 * Description : Test class for the AttackList class.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TestAttackList {

    @Test
    public void testAttackListSize() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack(
            "TestAttack1",
            "TestAttack1",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Attack attack2 = new Attack(
            "TestAttack2",
            "TestAttack2",
            Bugemon.BType.FLORA,
            "",
            20,
            effects
        );
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack(
            "TestAttack3",
            "TestAttack3",
            Bugemon.BType.FLORA,
            "",
            40,
            effects
        );
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertEquals(3, attackList.getAttacks().size());
    }

    @Test
    public void testShouldGetAttack1InCorrectOrder() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack(
            "TestAttack1",
            "TestAttack1",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Attack attack2 = new Attack(
            "TestAttack2",
            "TestAttack2",
            Bugemon.BType.FLORA,
            "",
            20,
            effects
        );
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack(
            "TestAttack3",
            "TestAttack3",
            Bugemon.BType.FLORA,
            "",
            40,
            effects
        );
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertEquals(attack1, attackList.getAttacks().get(0));
    }

    @Test
    public void testShouldGetAttack2InCorrectOrder() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack(
            "TestAttack1",
            "TestAttack1",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Attack attack2 = new Attack(
            "TestAttack2",
            "TestAttack2",
            Bugemon.BType.FLORA,
            "",
            20,
            effects
        );
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack(
            "TestAttack3",
            "TestAttack3",
            Bugemon.BType.FLORA,
            "",
            40,
            effects
        );
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertEquals(attack2, attackList.getAttacks().get(1));
    }

    @Test
    public void testShouldGetAttack3InCorrectOrder() {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack(
            "TestAttack1",
            "TestAttack1",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Attack attack2 = new Attack(
            "TestAttack2",
            "TestAttack2",
            Bugemon.BType.FLORA,
            "",
            20,
            effects
        );
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack(
            "TestAttack3",
            "TestAttack3",
            Bugemon.BType.FLORA,
            "",
            40,
            effects
        );
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertEquals(attack3, attackList.getAttacks().get(2));
    }

    @Test
    public void testEmptyAttackList() {
        AttackList attackList = new AttackList(List.of());
        assertEquals(0, attackList.getAttacks().size());
    }

    @Test
    public void testNullAttackList() {
        AttackList attackList = new AttackList((List<Attack>) null);
        assertNull(attackList.getAttacks());
    }
}
