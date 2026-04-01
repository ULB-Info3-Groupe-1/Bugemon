/**
 * Contains the JavaFX view classes responsible for rendering each application screen and forwarding user interactions
 * to the corresponding {@link ulb.controllers.Controller}.
 *
 * <h2>Package overview</h2>
 * <p>
 * Every class in this package represents a single screen or reusable UI component. Views load their layout from an FXML
 * resource file at construction time and expose a typed API that the controller layer calls to update displayed data
 * (e.g. refreshing a Bugemon's HP bar after a turn).
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 * <li>{@link ulb.views.View} — abstract base class. Loads the FXML file, creates the {@link javafx.scene.Scene}, and
 * binds the root pane's preferred size to the scene dimensions. Exposes {@link ulb.views.View#show(javafx.stage.Stage)}
 * so that controllers can display the screen through a uniform interface.</li>
 * <li>{@link ulb.views.MainMenuView} — the first screen the player sees; provides buttons for navigating to team
 * creation or quitting.</li>
 * <li>{@link ulb.views.CreateTeamView} — the team-assembly screen; hosts both the full Bugemon grid
 * ({@link ulb.views.AllBugemonsGridView}) and the current team panel ({@link ulb.views.BugemonTeamView}).</li>
 * <li>{@link ulb.views.CombatVictoryView} — shown when the player wins a combat session; presents a continue
 * button.</li>
 * <li>{@link ulb.views.CombatDefeatView} — shown when the player loses a combat session; presents retry and main-menu
 * buttons.</li>
 * <li>{@link ulb.views.LevelUpView} — shown after a victory when one or more Bugemons levelled up; presents three
 * stat-bonus choices.</li>
 * </ul>
 *
 * <h2>Reusable UI components</h2>
 * <ul>
 * <li>{@link ulb.views.ActionMenuView} — generic container for swappable action menus used inside the combat
 * screens.</li>
 * <li>{@link ulb.views.AllBugemonsGridView} — scrollable grid of all available Bugemons, used in the team-creation
 * screen.</li>
 * <li>{@link ulb.views.BugemonTeamView} — panel displaying the current team members; used in team creation and during
 * combat switches.</li>
 * <li>{@link ulb.views.DialogZoneView} — overlay banner used to display turn feedback messages (attack effectiveness,
 * KO notifications, …) during combat.</li>
 * </ul>
 *
 * <p>
 * Combat-specific views live in the sub-package {@link ulb.views.combat}.
 * </p>
 *
 * <h2>Design notes</h2>
 * <ul>
 * <li>All views are instantiated eagerly by their corresponding controller constructors, so every FXML resource is
 * loaded at application startup rather than on first use.</li>
 * <li>The two-way binding between a view and its controller is established by calling
 * {@code view.setController(controller)} at the end of the concrete controller's constructor.</li>
 * <li>The model layer is never accessed directly from a view; all data flows through the controller as
 * {@link ulb.common.dto.BugemonDTO} or other DTO types, keeping views decoupled from model internals.</li>
 * </ul>
 *
 * @see ulb.views.combat
 * @see ulb.controllers
 * @see ulb.common.dto.BugemonDTO
 */
package ulb.views;
