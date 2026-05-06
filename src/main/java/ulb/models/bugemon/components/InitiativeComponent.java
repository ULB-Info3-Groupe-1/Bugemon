package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/**
 * The initiative component of a Bugemon
 */
public class InitiativeComponent extends AbstractComponent {
    private int initiative;

    /**
     * Constructs a new InitiativeComponent
     *
     * @param initiative
     *            the current value of the initiative
     */
    public InitiativeComponent(int initiative) {
        this.initiative = initiative;
    }

    /**
     * Returns the current value of the initiative
     *
     * @return the current value of the initiative
     */
    public int getInitiative() {
        int computedInitiative = this.initiative;
        for (Modifier initiativeModifier : this.modifiers) {
            computedInitiative = initiativeModifier.apply(computedInitiative);
        }

        return computedInitiative;
    }

    /**
     * Increases the initiative value by the given amount
     *
     * @param amount
     *            the amount
     */
    public void increaseInitiative(int amount) {
        this.initiative += amount;
    }
}
