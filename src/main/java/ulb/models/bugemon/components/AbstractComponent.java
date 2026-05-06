package ulb.models.bugemon.components;

import java.util.ArrayList;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * A component is a collection of modifiers.
 */
public abstract class AbstractComponent {
    protected ArrayList<Modifier> modifiers = new ArrayList<>();

    /**
     * Decrements the number of remaining ticks for all modifiers and removes expired ones.
     */
    public void tick() {
        this.notifyTick();
        this.clearExpired();
    }

    private void notifyTick() {
        this.modifiers.forEach(Modifier::tick);
    }

    private void clearExpired() {
        this.modifiers.removeIf(Modifier::isExpired);
    }

    /**
     * Adds a modifier to the component.
     *
     * @param attackEffect
     *            the modifier
     */
    public void addModifier(Modifier attackEffect) {
        this.modifiers.add(attackEffect);
    }

    /**
     * Removes all modifiers that are maluses.
     */
    public void resetMalus() {
        this.modifiers.removeIf(Modifier::isMalus);
    }

    /**
     * Removes all modifiers.
     */
    public void resetModifiers() {
        this.modifiers.clear();
    }
}
