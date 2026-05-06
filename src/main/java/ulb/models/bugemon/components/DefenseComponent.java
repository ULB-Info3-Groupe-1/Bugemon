package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * The defense component of a Bugemon
 */
public class DefenseComponent extends AbstractComponent {
    private int defense;

    /**
     * Create a new defense component
     *
     * @param defense
     *            the defense
     */
    public DefenseComponent(int defense) {
        this.defense = defense;
    }

    /**
     * Get the current defense
     *
     * @return
     */
    public int getDefense() {
        int computedDefense = this.defense;
        for (Modifier defenseModifier : this.modifiers) {
            computedDefense = defenseModifier.apply(computedDefense);
        }

        return computedDefense;
    }

    /**
     * Increases the defense value by the given amount
     *
     * @param amount
     *            the amount
     */
    public void increaseDefense(int amount) {
        this.defense += amount;
    }
}
