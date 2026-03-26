package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

public class DefenseComponent extends AbstractComponent {
    private int defense;

    public DefenseComponent(int defense) {
        this.defense = defense;
    }

    public int getDefense() {
        int computedDefense = this.defense;
        for (Modifier defenseModifier : this.modifiers) {
            computedDefense = defenseModifier.apply(computedDefense);
        }

        return computedDefense;
    }

    public void increaseDefense(int amount) {
        this.defense += amount;
    }
}
