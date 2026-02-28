/**
 * File name : TestStats.java
 * Description : Test class for the Stats class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestStats {
    @Test
    public void testShouldGetAndSetHp() {
        Stats s = new Stats(99, 26, 44, 20);
        assertTrue(s.getHp() == 99);
        s.setHp(80);
        assertTrue(s.getHp() == 80);
    }

    @Test
    public void testShouldGetAndSetAttack() {
        Stats s = new Stats(99, 26, 44, 20);
        assertTrue(s.getAttack() == 26);
        s.setAttack(60);
        assertTrue(s.getAttack() == 60);
    }

    @Test
    public void testShouldGetAndSetDefense() {
        Stats s = new Stats(99, 26, 44, 20);
        assertTrue(s.getDefense() == 44);
        s.setDefense(50);
        assertTrue(s.getDefense() == 50);
    }

    @Test
    public void testShouldGetAndSetInitiative() {
        Stats s = new Stats(99, 26, 44, 20);
        assertTrue(s.getInitiative() == 20);
        s.setInitiative(25);
        assertTrue(s.getInitiative() == 25);
    }
}
