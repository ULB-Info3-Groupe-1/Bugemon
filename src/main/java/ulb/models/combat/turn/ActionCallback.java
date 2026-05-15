package ulb.models.combat.turn;

@FunctionalInterface
public interface ActionCallback {
    void onActionChosen(TurnAction action);
}
