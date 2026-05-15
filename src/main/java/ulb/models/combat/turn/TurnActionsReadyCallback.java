package ulb.models.combat.turn;

@FunctionalInterface
public interface TurnActionsReadyCallback {
    void onBothActionsReady(TurnAction playerAction, TurnAction opponentAction);
}
