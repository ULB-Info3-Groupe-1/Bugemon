package ulb.models.bugemon.components;

import ulb.models.bugemon.components.modifier.Modifier;

public class InitiativeComponent extends AbstractComponent {
    // FIXME: @SerializedName(...)
    private int initiative;

    public InitiativeComponent(int initiative) {
        this.initiative = initiative;
    }

    public int getInitiative() {
        int computedInitiative = this.initiative;
        for (Modifier initiativeModifier : this.modifiers) {
            computedInitiative = initiativeModifier.apply(computedInitiative);
        }

        return computedInitiative;
    }

    public void increaseInitiative(int amount) {
        this.initiative += amount;
    }
}
