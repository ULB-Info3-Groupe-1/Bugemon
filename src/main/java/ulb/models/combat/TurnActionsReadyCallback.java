package ulb.models.combat;

import ulb.models.trainer.TurnAction;

@FunctionalInterface
public interface TurnActionsReadyCallback {
    void onBothActionsReady(TurnAction playerAction, TurnAction opponentAction);
}
