package ulb.models.combat.strategy;

import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.utils.CombatContext;
import ulb.models.player.PlayerInputHandler;

public class PlayerStrategy implements CombatStrategy {

    private final PlayerInputHandler inputHandler;

    public PlayerStrategy(PlayerInputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void chooseAction(CombatContext context, ActionCallback callback) {
        this.inputHandler.requestActionChoice(context, callback);
    }

    @Override
    public void chooseSwitch(CombatContext context, ActionCallback callback) {
        this.inputHandler.requestSwitchChoice(context, callback);
    }
}
