package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/** Manages current and maximum HP, applying stacked modifiers on read. */
public class HealthComponent extends AbstractComponent {
    private int maxHp;
    private int hp;

    public HealthComponent(int hp, int maxHp) {
        this.hp = hp;
        this.maxHp = maxHp;
    }

    /** Returns current HP after applying all active modifiers; always {@code >= 0}. */
    public int getHp() {
        int computedHp = this.hp;
        for (Modifier healthModifier : this.modifiers) {
            computedHp = healthModifier.apply(computedHp);
        }

        return computedHp;
    }

    /** Returns the maximum HP; does not include active modifiers. */
    public int getMaxHp() {
        return this.maxHp;
    }

    /**
     * Increases the maximum hp value AND the current hp value by the given amount.
     */
    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
        this.hp += amount;
    }

    /** Reduces current HP by {@code amount}, clamped to {@code 0}. */
    public void decreaseHp(int amount) {
        this.hp = Math.max(0, this.hp - amount);
    }

    /** Increases current HP by {@code amount}, clamped to {@code maxHp}. */
    public void increaseHp(int amount) {
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    public void restoreHp() {
        this.hp = this.maxHp;
    }
}
