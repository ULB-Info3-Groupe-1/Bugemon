package ulb.models.bugemon.components;

import java.util.ArrayList;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * Base class for stat components supporting temporary {@link Modifier}s.
 * <p>
 * Modifiers are applied when computing the stat value (see concrete subclasses). Each call to {@link #tick()} advances
 * modifier durations and removes expired ones.
 */
public abstract class AbstractComponent {
    protected ArrayList<Modifier> modifiers = new ArrayList<>();

    /** Advances active modifiers by one tick and removes expired ones. */
    public void tick() {
        this.notifyTick();
        this.clearExpired();
    }

    /** Notifies all modifiers that a tick has occurred. */
    private void notifyTick() {
        this.modifiers.forEach(Modifier::tick);
    }

    /** Clears expired modifiers */
    private void clearExpired() {
        this.modifiers.removeIf(Modifier::isExpired);
    }

    /**
     * Adds a modifier to be applied on top of the base stat.
     *
     * @param attackEffect
     *            modifier to add
     */
    public void addModifier(Modifier attackEffect) {
        this.modifiers.add(attackEffect);
    }

    /** Removes every active Malus */
    public void resetMalus() {
        this.modifiers.removeIf(Modifier::isMalus);
    }

    /** Removes every active Modifier (including malus) */
    public void resetModifiers() {
        this.modifiers.clear();
    }
}
