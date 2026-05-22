package ulb.models.combat.strategy;

import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.utils.CombatContext;

/**
 * Strategy interface that decouples combat decision-making from the turn engine.
 *
 * <p>
 * Implementations range from human UI delegation ({@link PlayerStrategy}) to fully algorithmic approaches
 * ({@link AutoStrategy}, {@link MiniMaxStrategy}). The engine calls {@link #chooseAction} at the start of a turn and
 * {@link #chooseSwitch} whenever a forced switch is required; the chosen action is delivered asynchronously via the
 * {@link ActionCallback}.
 */
public interface CombatStrategy {
    /**
     * Requests that this strategy choose a turn action (attack, switch, item, or forfeit).
     *
     * @param ctx
     *            read-only view of the current combat state from this side's perspective
     * @param callback
     *            receiver that must be called exactly once with the chosen action
     */
    void chooseAction(CombatContext ctx, ActionCallback callback);

    /**
     * Requests that this strategy choose a replacement Bugemon after the active one faints.
     *
     * @param ctx
     *            read-only view of the current combat state
     * @param callback
     *            receiver that must be called exactly once with a
     *            {@link ulb.models.combat.turn.TurnAction.SwitchAction}
     */
    void chooseSwitch(CombatContext ctx, ActionCallback callback);
}
