package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

/** Initiative/speed stat component. */
public class InitiativeComponent extends AbstractComponent {
    private int initiative;

    /**
     * Constructs an InitiativeComponent.
     *
     * @param initiative
     *            base initiative value
     */
    public InitiativeComponent(int initiative) {
        this.initiative = initiative;
    }

    /** Returns the initiative taking the modifiers into account */
    public int getInitiative() {
        int computedInitiative = this.initiative;
        for (Modifier initiativeModifier : this.modifiers) {
            computedInitiative = initiativeModifier.apply(computedInitiative);
        }

        return computedInitiative;
    }

    /** Increases the base initiative by the given amount */
    public void increaseInitiative(int amount) {
        this.initiative += amount;
    }
}
