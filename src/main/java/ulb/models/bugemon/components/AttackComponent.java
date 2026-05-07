package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/** Attack stat component. */
public class AttackComponent extends AbstractComponent {
    private int attack;

    /**
     * Constructs an AttackComponent.
     *
     * @param attack
     *            base attack value
     */
    public AttackComponent(int attack) {
        this.attack = attack;
    }

    /** Returns the attack taking the modifiers into account */
    public int getAttack() {
        int computedAttack = this.attack;
        for (Modifier attackModifier : this.modifiers) {
            computedAttack = attackModifier.apply(computedAttack);
        }

        return computedAttack;
    }

    /** Increases the base attack by the given amount */
    public void increaseAttack(int amount) {
        this.attack += amount;
    }
}
