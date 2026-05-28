/**
 * Controllers for all combat-related screens.
 *
 * <p>
 * The two playable combat modes both extend {@link bugemon.client.controllers.combat.CombatController}, which handles
 * shared result routing. The automatic mode drives turns via a JavaFX {@link javafx.animation.Timeline} (one turn every
 * 3 s); the manual mode processes one turn per player action.
 *
 * <p>
 * Post-combat outcome screens are handled by {@link bugemon.client.controllers.combat.CombatDefeatController} and
 * {@link bugemon.client.controllers.combat.CombatVictoryController}.
 */
package bugemon.client.controllers.combat;
