package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon.components.modifier.Modifier;

public class TestInitiativeComponent {
    @Test
    public void testAddModifier() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier mod = new Modifier(5); // adds 5

        initiative.addModifier(mod);
        assertEquals(15, initiative.getInitiative());
    }

    @Test
    public void testAddMultipleModifiers() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier mod1 = new Modifier(5); // adds 5
        Modifier mod2 = new Modifier(3); // adds 3

        initiative.addModifier(mod1);
        initiative.addModifier(mod2);

        // 10 + 5 + 3 = 18
        assertEquals(18, initiative.getInitiative());
    }

    @Test
    public void testTemporaryModifier() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier tempMod = new Modifier(5, 1); // adds 5, lasts 1 tick

        initiative.addModifier(tempMod);
        assertEquals(15, initiative.getInitiative());

        initiative.tick(); // expires and removes the modifier
        assertEquals(10, initiative.getInitiative());
    }

    @Test
    public void testClearModifiers() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier mod1 = new Modifier(5);
        Modifier mod2 = new Modifier(3);

        initiative.addModifier(mod1);
        initiative.addModifier(mod2);
        assertEquals(18, initiative.getInitiative());

        initiative.resetModifiers();
        assertEquals(10, initiative.getInitiative());
    }

    @Test
    public void testNegativeModifier() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier debuff = new Modifier(-3); // reduces by 3

        initiative.addModifier(debuff);
        assertEquals(7, initiative.getInitiative());
    }

    @Test
    public void testMixedModifiers() {
        InitiativeComponent initiative = new InitiativeComponent(10);
        Modifier buff = new Modifier(5);
        Modifier debuff = new Modifier(-2);

        initiative.addModifier(buff);
        initiative.addModifier(debuff);

        // 10 + 5 - 2 = 13
        assertEquals(13, initiative.getInitiative());
    }
}
