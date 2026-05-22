/**
 * Models the turn lifecycle in a Bugemon combat encounter.
 *
 * <p>
 * A turn begins when both the player and the opponent commit a {@link ulb.models.combat.turn.TurnAction} (attack,
 * switch, use item, or forfeit). The actions are dispatched through {@link ulb.models.combat.turn.TurnActionVisitor}
 * implementations, which resolve them into an ordered list of {@link ulb.models.combat.turn.TurnStep} events. Callbacks
 * defined in this package ({@link ulb.models.combat.turn.ActionCallback},
 * {@link ulb.models.combat.turn.TurnActionsReadyCallback}, {@link ulb.models.combat.turn.TurnResolvedCallback})
 * decouple action submission from resolution and allow the view layer to animate each step sequentially.
 */
package ulb.models.combat.turn;
