package ulb.models.combat.turn;

import java.util.List;

import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;

/**
 * Visitor that resolves each {@link TurnAction} variant into an ordered list of {@link TurnStep} events.
 *
 * <p>
 * Implementations are responsible for applying game-logic side effects (damage calculation, Bugemon switching, item
 * consumption, etc.) and encoding the outcome as a sequence of steps that the view layer can animate.
 */
public interface TurnActionVisitor {

    /**
     * Resolves a forfeit action, ending the battle in favour of the opponent.
     *
     * @param action
     *            the forfeit action; never {@code null}
     * @return the list of steps that represent the forfeit outcome
     */
    List<TurnStep> visit(ForfeitAction action);

    /**
     * Resolves a switch action, swapping the active Bugemon for the specified target.
     *
     * @param action
     *            the switch action carrying the incoming Bugemon; never {@code null}
     * @return the list of steps that represent the switch sequence
     */
    List<TurnStep> visit(SwitchAction action);

    /**
     * Resolves an item action, applying the item's effect to the appropriate target.
     *
     * @param action
     *            the item action carrying the item to use; never {@code null}
     * @return the list of steps that represent item usage
     */
    List<TurnStep> visit(ItemAction action);

    /**
     * Resolves an attack action, computing damage and secondary effects.
     *
     * @param action
     *            the attack action carrying the attack to execute; never {@code null}
     * @return the list of steps that represent the attack sequence
     */
    List<TurnStep> visit(AttackAction action);

}
