/**
 * File name : TestBugemon.java
 * Description : Test class for the Bugemon class.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

import ulb.models.bugemon.exceptions.InvalidAttackCountException;
import ulb.utils.test.TestUtilsBugemons;

public class TestBugemon {
    @Test
    public void testBuilderNoIdThrows() {
        BugemonBuilder builder = new BugemonBuilder();
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void testBuilderRequiresExactlyThreeAttacks() {
        Attack a1 = TestUtilsBugemons.createAttack("a1", BugemonType.FLORA, 0);
        Attack a2 = TestUtilsBugemons.createAttack("a2", BugemonType.FLORA, 0);
        Attack a3 = TestUtilsBugemons.createAttack("a3", BugemonType.FLORA, 0);
        Attack a4 = TestUtilsBugemons.createAttack("a4", BugemonType.FLORA, 0);

        // two (or less) attacks does not work
        BugemonBuilder twoAttacks = new BugemonBuilder().name("two").hp(100).attackList(java.util.List.of(a1, a2));
        assertThrows(InvalidAttackCountException.class, twoAttacks::build);

        // three works
        BugemonBuilder threeAttacks = new BugemonBuilder().name("three").hp(100)
                .attackList(java.util.List.of(a1, a2, a3));
        threeAttacks.build();

        // four (or more) attacks does not work
        BugemonBuilder fourAttacks = new BugemonBuilder().name("four").hp(100)
                .attackList(java.util.List.of(a1, a2, a3, a4));
        assertThrows(InvalidAttackCountException.class, fourAttacks::build);
    }

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
        assertEquals(100, expectedBugemon1.getHp());
        expectedBugemon1.takeDamage(30);
        assertEquals(70, expectedBugemon1.getHp());
        expectedBugemon1.takeDamage(50);
        assertEquals(20, expectedBugemon1.getHp());
    }

    @Test
    public void testEquals() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("1");
        assertEquals(expectedBugemon2, expectedBugemon1);
    }

    @Test
    public void testHashCode() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("1");
        assertEquals(expectedBugemon1, expectedBugemon2);
        assertEquals(expectedBugemon1.hashCode(), expectedBugemon2.hashCode());
    }

    @Test
    public void testCorrectPathSprite() {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("1");
        String path = bugemon.getSpriteURL();
        URL resource = TestBugemon.class.getResource(path);
        assertNotNull(resource);
    }

    @Test
    public void testResetBugemon() {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("1");
        bugemon.takeDamage(50);
        assertEquals(50, bugemon.getHp());
        bugemon.restoreHp();
        assertEquals(100, bugemon.getHp());
    }
}
