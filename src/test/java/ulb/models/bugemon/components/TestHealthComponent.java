package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon.components.modifier.Modifier;

public class TestHealthComponent {
    @Test
    public void testCreationPartialHp() {
        HealthComponent health = new HealthComponent(30, 50);
        assertEquals(30, health.getHp());
        assertEquals(50, health.getMaxHp());
    }

    @Test
    public void testDecreaseHp() {
        HealthComponent health = new HealthComponent(50, 50);
        health.decreaseHp(20);
        assertEquals(30, health.getHp());
        assertEquals(50, health.getMaxHp());
    }

    @Test
    public void testDecreaseHpBelowZero() {
        HealthComponent health = new HealthComponent(50, 50);
        health.decreaseHp(60);
        assertEquals(0, health.getHp()); // should not go below 0
        assertEquals(50, health.getMaxHp());
    }

    @Test
    public void testDecreaseHpToZero() {
        HealthComponent health = new HealthComponent(50, 50);
        health.decreaseHp(50);
        assertEquals(0, health.getHp());
    }

    @Test
    public void testIncreaseHp() {
        HealthComponent health = new HealthComponent(30, 50);
        health.increaseHp(10);
        assertEquals(40, health.getHp());
    }

    @Test
    public void testIncreaseHpAboveMax() {
        HealthComponent health = new HealthComponent(45, 50);
        health.increaseHp(10);
        assertEquals(50, health.getHp()); // should not exceed maxHp
    }

    @Test
    public void testIncreaseHpToMax() {
        HealthComponent health = new HealthComponent(40, 50);
        health.increaseHp(10);
        assertEquals(50, health.getHp());
    }

    @Test
    public void testRestoreHp() {
        HealthComponent health = new HealthComponent(50, 50);
        health.decreaseHp(30);
        assertEquals(20, health.getHp());

        health.restoreHp();
        assertEquals(50, health.getHp());
    }

    @Test
    public void testAddModifier() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier mod = new Modifier(10); // adds 10

        health.addModifier(mod);
        assertEquals(60, health.getHp()); // 50 + 10
    }

    @Test
    public void testAddMultipleModifiers() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier mod1 = new Modifier(10);
        Modifier mod2 = new Modifier(5);

        health.addModifier(mod1);
        health.addModifier(mod2);

        // 50 + 10 + 5 = 65
        assertEquals(65, health.getHp());
    }

    @Test
    public void testTemporaryModifier() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier tempMod = new Modifier(10, 1); // adds 10, lasts 1 tick

        health.addModifier(tempMod);
        assertEquals(60, health.getHp());

        health.tick(); // expires and removes the modifier
        assertEquals(50, health.getHp());
    }

    @Test
    public void testClearModifiers() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier mod1 = new Modifier(10);
        Modifier mod2 = new Modifier(5);

        health.addModifier(mod1);
        health.addModifier(mod2);
        assertEquals(65, health.getHp());

        health.clearModifiers();
        assertEquals(50, health.getHp());
    }

    @Test
    public void testNegativeModifier() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier debuff = new Modifier(-10); // reduces by 10

        health.addModifier(debuff);
        assertEquals(40, health.getHp());
    }

    @Test
    public void testMixedModifiers() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier buff = new Modifier(15);
        Modifier debuff = new Modifier(-5);

        health.addModifier(buff);
        health.addModifier(debuff);

        // 50 + 15 - 5 = 60
        assertEquals(60, health.getHp());
    }

    @Test
    public void testGetMaxHpDoesNotUseModifiers() {
        HealthComponent health = new HealthComponent(50, 50);
        Modifier mod = new Modifier(10);

        health.addModifier(mod);

        assertEquals(50, health.getMaxHp());
    }
}
