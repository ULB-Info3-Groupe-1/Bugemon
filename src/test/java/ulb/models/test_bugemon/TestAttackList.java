/**
 * File name : TestAttackList.java
 * Description : Test class for the AttackList class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.test_bugemon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Effect;

public class TestAttackList {
    @Test
    public void testAttackListSize() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", ulb.models.bugemon.Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", ulb.models.bugemon.Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack("TestAttack3", "TestAttack3", ulb.models.bugemon.Type.FLORA, "", 40, effects);
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertTrue(attackList.getAttacks().size() == 3);
    }

    @Test
    public void testShouldGetAttack1InCorrectOrder() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", ulb.models.bugemon.Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", ulb.models.bugemon.Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack("TestAttack3", "TestAttack3", ulb.models.bugemon.Type.FLORA, "", 40, effects);
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertTrue(attackList.getAttacks().get(0) == attack1);
    }

    @Test
    public void testShouldGetAttack2InCorrectOrder() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", ulb.models.bugemon.Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", ulb.models.bugemon.Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack("TestAttack3", "TestAttack3", ulb.models.bugemon.Type.FLORA, "", 40, effects);
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertTrue(attackList.getAttacks().get(1) == attack2);
    }

    @Test
    public void testShouldGetAttack3InCorrectOrder() {
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", ulb.models.bugemon.Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", ulb.models.bugemon.Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack("TestAttack3", "TestAttack3", ulb.models.bugemon.Type.FLORA, "", 40, effects);
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertTrue(attackList.getAttacks().get(2) == attack3);
    }

    @Test
    public void testEmptyAttackList() {
        AttackList attackList = new AttackList(List.of());
        assertTrue(attackList.getAttacks().size() == 0);
    }

    @Test
    public void testNullAttackList() {
        AttackList attackList = new AttackList(null);
        assertNull(attackList.getAttacks());
    }
}
