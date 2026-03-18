package ulb.models.bugemon.components;

import java.util.ArrayList;

import ulb.models.bugemon.components.modifier.Modifier;

public abstract class AbstractComponent {
    protected ArrayList<Modifier> modifiers = new ArrayList<>();

    public void tick() {
        this.notifyTick();
        this.clearExpired();
    }

    public void notifyTick() {
        this.modifiers.forEach(Modifier::tick);
    }

    public void clearExpired() {
        this.modifiers.removeIf(Modifier::isExpired);
    }

    public void addModifier(Modifier attackEffect) {
        this.modifiers.add(attackEffect);
    }

    public void cleanModifiers() {
        this.modifiers.clear();
    }
}
