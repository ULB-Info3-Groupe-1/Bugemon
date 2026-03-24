package ulb.models.bugemon.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon.components.modifier.Modifier;

public class TestAbstractComponent {
    private static class TestableComponent extends AbstractComponent {}

    @Test
    public void testCreation() {
        TestableComponent component = new TestableComponent();
        assertEquals(0, component.modifiers.size());
    }

    @Test
    public void testAddAndClearMultipleModifiers() {
        TestableComponent component = new TestableComponent();

        Modifier mod1 = new Modifier(5);
        Modifier mod2 = new Modifier(3);

        component.addModifier(mod1);
        assertEquals(1, component.modifiers.size());

        component.addModifier(mod2);
        assertEquals(2, component.modifiers.size());

        component.clearModifiers();
        assertEquals(0, component.modifiers.size());
    }

    @Test
    public void testNotifyTick() {
        TestableComponent component = new TestableComponent();
        Modifier modifier = new Modifier(5, 2);

        component.addModifier(modifier);
        assertEquals(1, component.modifiers.size());

        component.tick();
        assertEquals(1, component.modifiers.size());

        component.tick();
        assertEquals(0, component.modifiers.size());
    }
}
