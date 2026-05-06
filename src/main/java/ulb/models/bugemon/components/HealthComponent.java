package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/** Health points (HP) stat component. */
public class HealthComponent extends AbstractComponent {
    private int maxHp;
    private int hp;

    /**
     * Constructs a HealthComponent.
     *
     * @param hp
     *            current HP value
     * @param maxHp
     *            maximum HP value
     */
    public HealthComponent(int hp, int maxHp) {
        this.hp = hp;
        this.maxHp = maxHp;
    }

    /** Returns the hp taking the modifiers into account */
    public int getHp() {
        int computedHp = this.hp;
        for (Modifier healthModifier : this.modifiers) {
            computedHp = healthModifier.apply(computedHp);
        }

        return computedHp;
    }

    /** Returns the maximum hp value */
    public int getMaxHp() {
        return this.maxHp;
    }

    /** Increases the maximum hp value AND the current hp value by the given amount. */
    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
        this.hp += amount;
    }

    /** Decreases the hp value by the given the amount. */
    public void decreaseHp(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    /** Increases the hp value by the given the amount while ensuring it does not exceed the maximum hp value. */
    public void increaseHp(int amount) {
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    /** Restores hp to its maximum value */
    public void restoreHp() {
        this.hp = this.maxHp;
    }
}
