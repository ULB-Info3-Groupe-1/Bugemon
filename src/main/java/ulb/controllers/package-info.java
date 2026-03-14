/**
 * Contains the controller classes that mediate between the view layer and the
 * model layer in the Bugemon application's MVC architecture.
 *
 * <h2>Package overview</h2>
 * <p>
 * Each controller is responsible for a single application screen. It receives
 * user-interaction callbacks from its associated {@link ulb.views.View}, updates
 * the model accordingly, and delegates screen-transition decisions to the
 * central {@link ulb.controllers.MetaController}.
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 *   <li>{@link ulb.controllers.Controller} — abstract generic base class.
 *       Binds a {@link ulb.controllers.MetaController} reference to a typed
 *       {@link ulb.views.View} and exposes a uniform
 *       {@link ulb.controllers.Controller#show(javafx.stage.Stage)} entry
 *       point used by the {@code MetaController} during screen transitions.</li>
 *   <li>{@link ulb.controllers.MetaController} — central orchestrator.
 *       Owns every concrete controller, loads game resources (JSON data files)
 *       at startup, and is the sole authority for navigating between
 *       {@link ulb.controllers.MetaController.Window} screens. All
 *       lower-level controllers hold a reference to it and call its methods
 *       to trigger navigation or access shared state.</li>
 *   <li>{@link ulb.controllers.MainMenuController} — manages the main menu
 *       screen; the entry point visible to the player on launch.</li>
 *   <li>{@link ulb.controllers.CreateTeamController} — manages the team
 *       creation screen where the player assembles their
 *       {@link ulb.models.bugemon_team.BugemonTeam} before entering combat.</li>
 *   <li>{@link ulb.controllers.CombatVictoryController} — manages the victory
 *       screen displayed when the player wins a combat session.</li>
 *   <li>{@link ulb.controllers.CombatDefeatController} — manages the defeat
 *       screen displayed when the player loses a combat session.</li>
 *   <li>{@link ulb.controllers.LevelUpController} — manages the level-up
 *       screen displayed after a victory when one or more Bugemons have
 *       gained enough XP to advance to the next level.</li>
 * </ul>
 *
 * <p>
 * Combat-specific controllers live in the sub-package
 * {@link ulb.controllers.combat}.
 * </p>
 *
 * <h2>Navigation model</h2>
 * <p>
 * Screen transitions are always initiated by calling
 * {@link ulb.controllers.MetaController#switchTo(ulb.controllers.MetaController.Window)}.
 * The available screens are enumerated by
 * {@link ulb.controllers.MetaController.Window}:
 * </p>
 * <ul>
 *   <li>{@code MAIN_MENU}</li>
 *   <li>{@code CREATE_TEAM}</li>
 *   <li>{@code MANUAL_COMBAT}</li>
 *   <li>{@code AUTOMATIC_COMBAT}</li>
 *   <li>{@code COMBAT_VICTORY}</li>
 *   <li>{@code COMBAT_DEFEAT}</li>
 *   <li>{@code LEVEL_UP}</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>All controllers are instantiated once by the
 *       {@link ulb.controllers.MetaController} constructor and reused across
 *       the application lifetime; they are not recreated on each screen
 *       transition.</li>
 *   <li>Each controller's view is also instantiated in the controller's
 *       constructor (eagerly), so all FXML resources are loaded at startup
 *       rather than on first use.</li>
 *   <li>The two-way binding between a controller and its view is established
 *       by calling {@code view.setController(this)} at the end of each
 *       concrete controller's constructor.</li>
 *   <li>Shared, read-only application state (e.g., the list of all available
 *       Bugemons, alert display) is accessed through the
 *       {@link ulb.controllers.MetaController} rather than stored in individual
 *       controllers.</li>
 * </ul>
 *
 * @see ulb.controllers.combat
 * @see ulb.views
 * @see ulb.models
 */
package ulb.controllers;
