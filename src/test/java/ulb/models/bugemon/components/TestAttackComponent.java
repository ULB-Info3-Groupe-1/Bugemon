package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon.components.modifier.Modifier;

public class TestAttackComponent {
    @Test
    public void testAddModifier() {
        AttackComponent attack = new AttackComponent(10);
        Modifier mod = new Modifier(5); // adds 5

        attack.addModifier(mod);
        assertEquals(15, attack.getAttack());
    }

    @Test
    public void testAddMultipleModifiers() {
        AttackComponent attack = new AttackComponent(10);
        Modifier mod1 = new Modifier(5); // adds 5
        Modifier mod2 = new Modifier(3); // adds 3

        attack.addModifier(mod1);
        attack.addModifier(mod2);

        // 10 + 5 + 3 = 18
        assertEquals(18, attack.getAttack());
    }

    @Test
    public void testTemporaryModifier() {
        AttackComponent attack = new AttackComponent(10);
        Modifier tempMod = new Modifier(5, 1); // adds 5, lasts 1 tick

        attack.addModifier(tempMod);
        assertEquals(15, attack.getAttack());

        attack.tick(); // expires and removes the modifier
        assertEquals(10, attack.getAttack());
    }

    @Test
    public void testClearModifiers() {
        AttackComponent attack = new AttackComponent(10);
        Modifier mod1 = new Modifier(5);
        Modifier mod2 = new Modifier(3);

        attack.addModifier(mod1);
        attack.addModifier(mod2);
        assertEquals(18, attack.getAttack());

        attack.clearModifiers();
        assertEquals(10, attack.getAttack());
    }

    @Test
    public void testNegativeModifier() {
        AttackComponent attack = new AttackComponent(10);
        Modifier debuff = new Modifier(-3); // reduces by 3

        attack.addModifier(debuff);
        assertEquals(7, attack.getAttack());
    }

    @Test
    public void testMixedModifiers() {
        AttackComponent attack = new AttackComponent(10);
        Modifier buff = new Modifier(5);
        Modifier debuff = new Modifier(-2);

        attack.addModifier(buff);
        attack.addModifier(debuff);

        // 10 + 5 - 2 = 13
        assertEquals(13, attack.getAttack());
    }
}
