package ulb.models.combat.turn;

/**
 * Callback invoked when a combatant has chosen its action for the current turn.
 *
 * <p>
 * Implemented as a functional interface so it can be supplied as a lambda wherever the combat engine or AI needs to
 * notify a listener that a {@link TurnAction} has been selected and is ready for resolution.
 */
@FunctionalInterface
public interface ActionCallback {

    /**
     * Called when a combatant has committed to an action.
     *
     * @param action
     *            the action chosen for this turn; never {@code null}
     */
    void onActionChosen(TurnAction action);
}
