package ulb.models.bugemon.components;

import java.util.ArrayList;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * Base for all stat components. Maintains a list of temporary {@link Modifier}s; {@link #tick()} advances and prunes
 * expired ones each turn.
 */
public abstract class AbstractComponent {
    protected ArrayList<Modifier> modifiers = new ArrayList<>();

    /** Decrements all modifier timers and removes those that have expired. Must be called once per combat turn. */
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

    public void addModifier(Modifier attackEffect) {
        this.modifiers.add(attackEffect);
    }

    /** Removes only negative modifiers (malus), leaving positive buffs intact. */
    public void resetMalus() {
        this.modifiers.removeIf(Modifier::isMalus);
    }

    /** Removes all active modifiers unconditionally (e.g. at end of combat). */
    public void resetModifiers() {
        this.modifiers.clear();
    }
}
