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

import org.junit.Test;

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
