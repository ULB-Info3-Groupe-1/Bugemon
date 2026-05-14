package ulb.models.combat;

import ulb.models.trainer.TurnAction;

@FunctionalInterface
public interface TurnCallback {
  void onBothActionsReady(TurnAction playerAction, TurnAction opponentAction);
}

