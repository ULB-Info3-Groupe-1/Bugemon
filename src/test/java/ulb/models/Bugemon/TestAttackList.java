/**
 * Nom du fichier : TestAttackList.java
 * Description : Test class for the AttackList class.
 * 
 * @author Liefferinckx Romain
 * @date 24 févr. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Effect;

public class TestAttackList {
    @Test
    public void testCreationAttackList() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        assertTrue(attackList.getAttacks().size() == 2);
        assertTrue(attackList.getAttacks().get(0) == attack1);
        assertTrue(attackList.getAttacks().get(1) == attack2);
    }

    @Test
    public void testGettersAndSetters() {
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10, "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Attack attack3 = new Attack("TestAttack3", "TestAttack3", "Flora", "", 40, effect);
        attackList.setAttacks(List.of(attack1, attack2, attack3));
        assertTrue(attackList.getAttacks().size() == 3);
        assertTrue(attackList.getAttacks().get(0) == attack1);
        assertTrue(attackList.getAttacks().get(1) == attack2);
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
