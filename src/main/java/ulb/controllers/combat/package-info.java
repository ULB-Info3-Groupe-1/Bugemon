/**
 * Controllers for all combat-related screens.
 *
 * <p>
 * The two playable combat modes both extend {@link ulb.controllers.combat.CombatController}, which handles shared
 * result routing. The automatic mode drives turns via a JavaFX {@link javafx.animation.Timeline} (one turn every 3 s);
 * the manual mode processes one turn per player action.
 *
 * <p>
 * Post-combat outcome screens are handled by {@link ulb.controllers.combat.CombatDefeatController} and
 * {@link ulb.controllers.combat.CombatVictoryController}.
 */
package ulb.controllers.combat;
