package ulb.models.player;

import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.utils.CombatContext;

public interface PlayerInputHandler {

    void requestActionChoice(CombatContext context, ActionCallback callback);

    void requestSwitchChoice(CombatContext context, ActionCallback callback);
}
