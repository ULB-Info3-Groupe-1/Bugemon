/**
 * Contains the JavaFX view classes responsible for rendering the combat screens and their reusable
 * sub-components in the Bugemon application.
 *
 * <h2>Package overview</h2>
 * <p>
 * This sub-package of {@link ulb.views} groups every view class involved in a combat session. Each
 * class either represents a full combat screen or a reusable UI widget that is embedded inside one.
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 * <li>{@link ulb.views.combat.CombatView} — abstract base class for all combat screens. Loads the
 * shared {@code Combat.fxml} layout, declares the FXML-injected components common to both combat
 * modes (Bugemon info panels, sprite images, action-menu container, team-switcher pane, dialog
 * zone), and exposes the public API that the controller layer uses to update the UI after each
 * turn.</li>
 * <li>{@link ulb.views.combat.AutomaticCombatView} — concrete view for the fully automated combat
 * mode. Hides the action menu and team-switcher pane, since the player has no input during an
 * automatic session.</li>
 * <li>{@link ulb.views.combat.ManualCombatView} — concrete view for the player-controlled combat
 * mode. Shows the main action menu by default and wires up the attack-selection and Bugemon-switch
 * panels.</li>
 * </ul>
 *
 * <h2>Reusable combat UI components</h2>
 * <ul>
 * <li>{@link ulb.views.combat.BugemonInfoView} — compact panel displaying a Bugemon's name,
 * elemental type, current HP, and HP bar. Shown for both the player's and the opponent's active
 * Bugemon.</li>
 * <li>{@link ulb.views.combat.MainActionMenu} — the top-level action menu offering the player the
 * choice to attack, switch their active Bugemon, or forfeit the match.</li>
 * <li>{@link ulb.views.combat.AttackActionMenu} — the attack-selection menu listing all moves
 * available for the player's currently active Bugemon, annotated with their type effectiveness
 * against the current opponent.</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 * <li>All combat view classes share a single FXML layout ({@code Combat.fxml}) loaded by
 * {@link ulb.views.combat.CombatView}. Mode-specific differences are handled by
 * {@link ulb.views.combat.CombatView#initCombatMode()}, which each concrete subclass implements to
 * show or hide the regions relevant to its mode.</li>
 * <li>The controller layer interacts with the combat UI exclusively through the public methods of
 * {@link ulb.views.combat.CombatView} and its subclasses, keeping all JavaFX node manipulation out
 * of the controller.</li>
 * <li>The model layer is never accessed directly from a view; data flows through the controller as
 * {@link ulb.common.dto.BugemonDTO} instances or primitive values, keeping views decoupled from
 * model internals.</li>
 * </ul>
 *
 * @see ulb.views
 * @see ulb.controllers.combat
 * @see ulb.common.dto.BugemonDTO
 */
package ulb.views.combat;
