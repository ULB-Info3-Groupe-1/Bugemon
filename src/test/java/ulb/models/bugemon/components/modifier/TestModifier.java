package ulb.models.bugemon.components.modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestModifier {
    @Test
    public void testModifierPermanentNoExpiry() {
        Modifier mod = new Modifier(1); // permanent modifier that adds 1

        for (int i = 0; i < 3; i++) {
            mod.tick();
        }

        assertFalse(mod.isExpired());
    }

    @Test
    public void testTemporaryModifierExpiry() {
        Modifier mod = new Modifier(3, 1);
        mod.tick();
        assertTrue(mod.isExpired());
    }

    @Test
    public void testApply() {
        // this modifier adds 3
        Modifier mod = new Modifier(3);

        // expected: 3+2=5
        assertEquals(5, mod.apply(2));
    }

    @Test
    public void testApplyExpiredThrows() {
        Modifier mod = new Modifier(3, 1);
        mod.tick();

        assertThrows(IllegalStateException.class, () -> { mod.apply(5); });
    }
}
