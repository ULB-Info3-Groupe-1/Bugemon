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

import ulb.models.bugemon.Effect;
import ulb.models.bugemon.Stats;
import ulb.models.bugemon.Type;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;

public class TestBugemon {
    @Test
    public void testIsAlive() {
        Stats stats = new Stats(100, 20, 10, 5);
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10, "1 turn");
        List<Effect> effects = List.of(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", Type.FLORA, "testSprite", stats, attackList,
                false);
        assertTrue(bugemon.isAlive());
        bugemon.takeDamage(50);
        assertTrue(bugemon.isAlive());
        bugemon.takeDamage(50);
        assertFalse(bugemon.isAlive());
    }

    @Test
    public void testTakeDamage() {
        Stats stats = new Stats(100, 20, 10, 5);
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10,
                "1 turn");
        List<Effect> effects = List.of(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", Type.FLORA, "testSprite", stats, attackList,
                false);
        assertTrue(bugemon.getStats().getHp() == 100);
        bugemon.takeDamage(30);
        assertTrue(bugemon.getStats().getHp() == 70);
        bugemon.takeDamage(50);
        assertTrue(bugemon.getStats().getHp() == 20);
    }

    @Test
    public void testCreationBugemon() {
        Stats stats = new Stats(100, 20, 10, 5);
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10,
                "1 turn");
        List<Effect> effects = List.of(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", Type.FLORA, "testSprite", stats, attackList,
                false);
        assertEquals("TestBugemon1", bugemon.getId());
        assertEquals("TestBugemon1", bugemon.getName());
        assertEquals(Type.FLORA, bugemon.getType());
        assertEquals("testSprite", bugemon.getSpriteURL());
        assertEquals(stats, bugemon.getStats());
        assertEquals(attackList, bugemon.getAttackList());
        assertFalse(bugemon.isStarter());
    }
}
