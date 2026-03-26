package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

public class HealthComponent extends AbstractComponent {
    private int maxHp;
    private int hp;

    public HealthComponent(int hp, int maxHp) {
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public int getHp() {
        int computedHp = this.hp;
        for (Modifier healthModifier : this.modifiers) {
            computedHp = healthModifier.apply(computedHp);
        }

        return computedHp;
    }

    // NOTE: this method does not take the effects into account
    public int getMaxHp() {
        return this.maxHp;
    }

    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
    }

    public void decreaseHp(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    public void increaseHp(int amount) {
        this.hp = Math.min(maxHp, this.hp + amount);
    }

    public void restoreHp() {
        this.hp = this.maxHp;
    }
}
