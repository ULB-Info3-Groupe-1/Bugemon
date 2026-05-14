package ulb.models.combat;

import ulb.models.trainer.TurnAction;

@FunctionalInterface
public interface ActionCallback {
    void onActionChosen(TurnAction action);
}
