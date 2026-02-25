/**
 * Nom du fichier : TestBugemon.java
 * Description : Test class for the Bugemon class.
 * 
 * @author Liefferinckx Romain
 * @date 24 févr. 2026
 * @version 1.0
 */

package ulb.models.Bugemon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.List;

import org.junit.Test;

public class TestBugemon {
    @Test
    public void testIsAlive() {
        Stats stats = new Stats(100, 20, 10, 5);
        ulb.models.Bugemon.Effect effect = new ulb.models.Bugemon.Effect("TestEffect", "TestEffect", "Flora", 10,
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
        ulb.models.Bugemon.Effect effect = new ulb.models.Bugemon.Effect("TestEffect", "TestEffect", "Flora", 10,
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
        ulb.models.Bugemon.Effect effect = new ulb.models.Bugemon.Effect("TestEffect", "TestEffect", "Flora", 10,
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
}
