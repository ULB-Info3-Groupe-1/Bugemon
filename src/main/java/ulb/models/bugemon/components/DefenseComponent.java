package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/** Defense stat component. */
public class DefenseComponent extends AbstractComponent {
    private int defense;

    /**
     * Constructs a DefenseComponent.
     *
     * @param defense
     *            base defense value
     */
    public DefenseComponent(int defense) {
        this.defense = defense;
    }

    /** Returns the defense taking the modifiers into account */
    public int getDefense() {
        int computedDefense = this.defense;
        for (Modifier defenseModifier : this.modifiers) {
            computedDefense = defenseModifier.apply(computedDefense);
        }

        return computedDefense;
    }

    /** Increases the base defense by the given amount */
    public void increaseDefense(int amount) {
        this.defense += amount;
    }
}
