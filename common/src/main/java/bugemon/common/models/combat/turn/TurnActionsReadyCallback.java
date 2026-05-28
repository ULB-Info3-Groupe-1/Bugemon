package bugemon.common.models.combat.turn;

/**
 * Callback invoked once both the player and the opponent have committed their actions for the current turn.
 *
 * <p>
 * The combat engine fires this callback to signal that turn resolution can begin. Implementations typically hand off
 * the paired actions to a {@link TurnActionVisitor} for priority evaluation and step generation.
 */
@FunctionalInterface
public interface TurnActionsReadyCallback {

    /**
     * Called when both combatants have chosen their actions.
     *
     * @param playerAction
     *            the action chosen by the player; never {@code null}
     * @param opponentAction
     *            the action chosen by the opponent; never {@code null}
     */
    void onBothActionsReady(TurnAction playerAction, TurnAction opponentAction);
}
