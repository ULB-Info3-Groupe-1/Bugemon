package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

public class AttackComponent extends AbstractComponent {
    private int attack;

    public AttackComponent(int attack) {
        this.attack = attack;
    }

    public int getAttack() {
        int computedAttack = this.attack;
        for (Modifier attackModifier : this.modifiers) {
            computedAttack = attackModifier.apply(computedAttack);
        }

        return computedAttack;
    }

    public void increaseAttack(int amount) {
        this.attack += amount;
    }
}
