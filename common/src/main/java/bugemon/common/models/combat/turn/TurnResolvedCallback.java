package bugemon.common.models.combat.turn;

import java.util.List;

/**
 * Callback invoked after a turn has been fully resolved into its constituent steps.
 *
 * <p>
 * The combat engine calls this callback once all {@link TurnAction} instances for the current turn have been processed
 * by a {@link TurnActionVisitor}. The resulting {@link TurnStep} list is passed to the view layer so that each event
 * (attack, KO, switch, heal, etc.) can be presented to the player in sequence.
 */
@FunctionalInterface
public interface TurnResolvedCallback {

    /**
     * Called when all steps for the current turn are ready for presentation.
     *
     * @param turnSteps
     *            the ordered list of steps describing the turn outcome; never {@code null}, may be empty if the turn
     *            produced no observable events
     */
    void onTurnResolved(List<TurnStep> turnSteps);
}
