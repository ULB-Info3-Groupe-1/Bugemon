package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * The health component of a Bugemon
 */
public class HealthComponent extends AbstractComponent {
    private int maxHp;
    private int hp;

    /**
     * Constructor
     *
     * @param hp
     *            the current HP
     * @param maxHp
     *            the maximum HP
     */
    public HealthComponent(int hp, int maxHp) {
        this.hp = hp;
        this.maxHp = maxHp;
    }

    /**
     * Returns the current HP
     *
     * @return the current HP
     */
    public int getHp() {
        int computedHp = this.hp;
        for (Modifier healthModifier : this.modifiers) {
            computedHp = healthModifier.apply(computedHp);
        }

        return computedHp;
    }

    // NOTE: this method does not take the effects into account
    /**
     * Returns the maximum HP
     *
     * @return the maximum HP
     */
    public int getMaxHp() {
        return this.maxHp;
    }

    /**
     * Increases the maximum hp value AND the current hp value by the given amount.
     *
     * @param amount
     *            the amount
     */
    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
        this.hp += amount;
    }

    /**
     * Decreases the hp value by the given amount
     *
     * @param amount
     *            the amount
     */
    public void decreaseHp(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    /**
     * Increases the hp value by the given amount
     *
     * @param amount
     *            the amount
     */
    public void increaseHp(int amount) {
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    /**
     * Restores the hp value to the maximum
     */
    public void restoreHp() {
        this.hp = this.maxHp;
    }
}
