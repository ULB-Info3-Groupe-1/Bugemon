package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon.components.modifier.Modifier;

public class TestDefenseComponent {
    @Test
    public void testAddModifier() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier mod = new Modifier(5); // adds 5

        defense.addModifier(mod);
        assertEquals(15, defense.getDefense());
    }

    @Test
    public void testAddMultipleModifiers() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier mod1 = new Modifier(5); // adds 5
        Modifier mod2 = new Modifier(3); // adds 3

        defense.addModifier(mod1);
        defense.addModifier(mod2);

        // 10 + 5 + 3 = 18
        assertEquals(18, defense.getDefense());
    }

    @Test
    public void testTemporaryModifier() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier tempMod = new Modifier(5, 1); // adds 5, lasts 1 tick

        defense.addModifier(tempMod);
        assertEquals(15, defense.getDefense());

        defense.tick(); // expires and removes the modifier
        assertEquals(10, defense.getDefense());
    }

    @Test
    public void testCleanModifiers() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier mod1 = new Modifier(5);
        Modifier mod2 = new Modifier(3);

        defense.addModifier(mod1);
        defense.addModifier(mod2);
        assertEquals(18, defense.getDefense());

        defense.resetModifiers();
        assertEquals(10, defense.getDefense());
    }

    @Test
    public void testNegativeModifier() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier debuff = new Modifier(-3); // reduces by 3

        defense.addModifier(debuff);
        assertEquals(7, defense.getDefense());
    }

    @Test
    public void testMixedModifiers() {
        DefenseComponent defense = new DefenseComponent(10);
        Modifier buff = new Modifier(5);
        Modifier debuff = new Modifier(-2);

        defense.addModifier(buff);
        defense.addModifier(debuff);

        // 10 + 5 - 2 = 13
        assertEquals(13, defense.getDefense());
    }
}
