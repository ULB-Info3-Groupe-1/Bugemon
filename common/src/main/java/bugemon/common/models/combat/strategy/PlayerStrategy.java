package bugemon.common.models.combat.strategy;

import bugemon.common.models.combat.turn.ActionCallback;
import bugemon.common.models.combat.utils.CombatContext;
import bugemon.common.models.player.PlayerInputHandler;

/**
 * {@link CombatStrategy} that delegates all decisions to a {@link PlayerInputHandler}, bridging the combat model to the
 * player-facing UI.
 *
 * <p>
 * Both {@link #chooseAction} and {@link #chooseSwitch} forward the request to the handler, which is responsible for
 * surfacing the choice to the player and invoking the callback when a selection is made.
 */
public class PlayerStrategy implements CombatStrategy {

    private final PlayerInputHandler inputHandler;

    /**
     * Creates a {@code PlayerStrategy} backed by the given UI input handler.
     *
     * @param inputHandler
     *            handler that requests player input and delivers it via the callback
     */
    public PlayerStrategy(PlayerInputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /** {@inheritDoc} */
    @Override
    public void chooseAction(CombatContext context, ActionCallback callback) {
        this.inputHandler.requestActionChoice(context, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void chooseSwitch(CombatContext context, ActionCallback callback) {
        this.inputHandler.requestSwitchChoice(context, callback);
    }
}
