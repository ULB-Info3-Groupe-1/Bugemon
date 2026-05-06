package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * The attack component of a Bugemon
 */
public class AttackComponent extends AbstractComponent {
    private int attack;

    /**
     * Creates an attack component
     *
     * @param attack
     *            the attack
     */
    public AttackComponent(int attack) {
        this.attack = attack;
    }

    /**
     * Get the attack value
     *
     * @return the attack
     */
    public int getAttack() {
        int computedAttack = this.attack;
        for (Modifier attackModifier : this.modifiers) {
            computedAttack = attackModifier.apply(computedAttack);
        }

        return computedAttack;
    }

    /**
     * Increases the attack value by the given amount
     *
     * @param amount
     *            the amount
     */
    public void increaseAttack(int amount) {
        this.attack += amount;
    }
}
