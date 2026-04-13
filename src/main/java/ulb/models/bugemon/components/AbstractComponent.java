package ulb.models.bugemon.components;

import java.util.ArrayList;

import ulb.models.bugemon.components.modifier.Modifier;

public abstract class AbstractComponent {
    protected ArrayList<Modifier> modifiers = new ArrayList<>();

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

    public void resetMalus() {
        this.modifiers.removeIf(Modifier::isMalus);
    }

    public void resetModifiers() {
        this.modifiers.clear();
    }
}
