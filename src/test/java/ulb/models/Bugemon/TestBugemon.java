/**
 * File name : TestBugemon.java
 * Description : Test class for the Bugemon class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.List;

import org.junit.Test;

import ulb.bugemon.models.Attack;
import ulb.bugemon.models.AttackList;
import ulb.bugemon.models.Bugemon;
import ulb.bugemon.models.Stats;

public class TestBugemon {
    @Test
    public void testIsAlive() {
        Stats stats = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats, attackList, false);
        assertTrue(bugemon.isAlive());
        bugemon.takeDamage(50);
        assertTrue(bugemon.isAlive());
        bugemon.takeDamage(50);
        assertFalse(bugemon.isAlive());
    }

    @Test
    public void testTakeDamage() {
        Stats stats = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats, attackList, false);
        assertTrue(bugemon.getStats().getHp() == 100);
        bugemon.takeDamage(30);
        assertTrue(bugemon.getStats().getHp() == 70);
        bugemon.takeDamage(50);
        assertTrue(bugemon.getStats().getHp() == 20);
    }

    @Test
    public void testCreationBugemon() {
        Stats stats = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats, attackList, false);
        assertEquals("TestBugemon1", bugemon.getId());
        assertEquals("TestBugemon1", bugemon.getName());
        assertEquals("Flora", bugemon.getType());
        assertEquals("testSprite", bugemon.getSprite());
        assertEquals(stats, bugemon.getStats());
        assertEquals(attackList, bugemon.getAttackList());
        assertFalse(bugemon.isStarter());
    }

    @Test
    public void testEquals() {
        Stats stats1 = new Stats(100, 20, 10, 5);
        Stats stats2 = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect1 = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        ulb.bugemon.models.Effect effect2 = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect1);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect1);
        Attack attack3 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect2);
        Attack attack4 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect2);
        AttackList attackList1 = new AttackList(List.of(attack1, attack2));
        AttackList attackList2 = new AttackList(List.of(attack3, attack4));
        Bugemon bugemon1 = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats1, attackList1,
                false);
        Bugemon bugemon2 = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats2, attackList2,
                false);
        assertTrue(bugemon1.equals(bugemon2));
    }

    @Test
    public void testHashCode() {
        Stats stats1 = new Stats(100, 20, 10, 5);
        Stats stats2 = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect1 = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        ulb.bugemon.models.Effect effect2 = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect1);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect1);
        Attack attack3 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect2);
        Attack attack4 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect2);
        AttackList attackList1 = new AttackList(List.of(attack1, attack2));
        AttackList attackList2 = new AttackList(List.of(attack3, attack4));
        Bugemon bugemon1 = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats1, attackList1,
                false);
        Bugemon bugemon2 = new Bugemon("TestBugemon1", "TestBugemon1", "Flora", "testSprite", stats2, attackList2,
                false);
        assertTrue(bugemon1.hashCode() == bugemon2.hashCode());
    }
}
