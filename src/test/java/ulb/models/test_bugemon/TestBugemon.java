/**
 * File name : TestBugemon.java
 * Description : Test class for the Bugemon class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.test_bugemon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.Stats;
import ulb.utils.TestUtilsBugemons;

public class TestBugemon {
        @Test
        public void testIsAlive() {
                Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
                assertTrue(expectedBugemon1.isAlive());
                expectedBugemon1.takeDamage(50);
                assertTrue(expectedBugemon1.isAlive());
                expectedBugemon1.takeDamage(50);
                assertFalse(expectedBugemon1.isAlive());
        }

        @Test
        public void testTakeDamage() {
                Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
                assertTrue(expectedBugemon1.getStats().getHp() == 100);
                expectedBugemon1.takeDamage(30);
                assertTrue(expectedBugemon1.getStats().getHp() == 70);
                expectedBugemon1.takeDamage(50);
                assertTrue(expectedBugemon1.getStats().getHp() == 20);
        }

        @Test
        public void testCreationBugemon() {
                Stats stats = new Stats(100, 20, 10, 5);
                Effect effect = new Effect("TestEffect", "TestEffect", "Flora",
                                10,
                                "1 turn");
                List<Effect> effectList = new ArrayList<>();
                effectList.add(effect);
                Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effectList);
                Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effectList);
                AttackList attackList = new AttackList(List.of(attack1, attack2));
                Bugemon bugemon = new Bugemon("TestBugemon1", "TestBugemon1", Bugemon.Type.FLORA, "testSprite", stats,
                                attackList, false);
                assertEquals("TestBugemon1", bugemon.getId());
                assertEquals("TestBugemon1", bugemon.getName());
                assertEquals(Bugemon.Type.FLORA, bugemon.getType());
                assertEquals("testSprite", bugemon.getSpriteURL());
                assertEquals(stats, bugemon.getStats());
                assertEquals(attackList, bugemon.getAttackList());
                assertFalse(bugemon.isStarter());
        }

        @Test
        public void testEquals() {
                Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
                Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("1");
                assertTrue(expectedBugemon1.equals(expectedBugemon2));
        }

        @Test
        public void testHashCode() {
                Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
                Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("1");
                assertTrue(expectedBugemon1.hashCode() == expectedBugemon2.hashCode());
        }
}
