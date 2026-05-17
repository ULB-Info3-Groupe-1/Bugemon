/**
 * Combat controllers for the two combat modes (automatic and manual). Each extends
 * {@link ulb.controllers.combat.CombatController}, which handles shared result routing.
 *
 * The automatic mode drives turns via a JavaFX {@link javafx.animation.Timeline} (one turn every 3 s); the manual mode
 * processes one turn per player action.
 */
package ulb.controllers.combat;
