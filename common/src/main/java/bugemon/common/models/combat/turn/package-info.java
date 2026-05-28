/**
 * Models the turn lifecycle in a Bugemon combat encounter.
 *
 * <p>
 * A turn begins when both the player and the opponent commit a {@link bugemon.common.models.combat.turn.TurnAction} (attack,
 * switch, use item, or forfeit). The actions are dispatched through {@link bugemon.common.models.combat.turn.TurnActionVisitor}
 * implementations, which resolve them into an ordered list of {@link bugemon.common.models.combat.turn.TurnStep} events. Callbacks
 * defined in this package ({@link bugemon.common.models.combat.turn.ActionCallback},
 * {@link bugemon.common.models.combat.turn.TurnActionsReadyCallback}, {@link bugemon.common.models.combat.turn.TurnResolvedCallback})
 * decouple action submission from resolution and allow the view layer to animate each step sequentially.
 */
package bugemon.common.models.combat.turn;
