/**
 * File name : TestStats.java
 * Description : Test class for the Stats class.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStats {
    @Test
    public void testShouldGetAndSetHp() {
        Stats s = new Stats(99, 26, 44, 20);
        assertEquals(99, s.getHp());
        s.setHp(80);
        assertEquals(80, s.getHp());
    }

    @Test
    public void testShouldGetAndSetAttack() {
        Stats s = new Stats(99, 26, 44, 20);
        assertEquals(26, s.getAttack());
        s.setAttack(60);
        assertEquals(60, s.getAttack());
    }

    @Test
    public void testShouldGetAndSetDefense() {
        Stats s = new Stats(99, 26, 44, 20);
        assertEquals(44, s.getDefense());
        s.setDefense(50);
        assertEquals(50, s.getDefense());
    }

    @Test
    public void testShouldGetAndSetInitiative() {
        Stats s = new Stats(99, 26, 44, 20);
        assertEquals(20, s.getInitiative());
        s.setInitiative(25);
        assertEquals(25, s.getInitiative());
    }
}
