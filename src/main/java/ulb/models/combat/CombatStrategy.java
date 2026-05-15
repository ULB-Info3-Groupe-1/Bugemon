package ulb.models.combat;

import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.utils.CombatContext;

public interface CombatStrategy {
    void chooseAction(CombatContext ctx, ActionCallback callback);

    void chooseSwitch(CombatContext ctx, ActionCallback callback);
}
