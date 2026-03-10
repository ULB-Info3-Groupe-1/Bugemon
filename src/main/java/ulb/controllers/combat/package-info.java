/**
 * Contains the controller classes responsible for managing combat screens in
 * the Bugemon application.
 *
 * <h2>Package overview</h2>
 * <p>
 * This sub-package of {@link ulb.controllers} groups the controllers that drive
 * the two combat modes offered by the game. Each controller extends the abstract
 * {@link ulb.controllers.combat.CombatController} base class, which handles
 * common initialisation (pre-populating the view with placeholder
 * {@link ulb.models.bugemon.Bugemon}s) and provides the shared
 * {@link ulb.controllers.combat.CombatController#handleCombatResult(ulb.models.trainer.Trainer,
 * ulb.models.trainer.Trainer)} helper used to navigate to the appropriate outcome screen once a
 * session ends.
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 *   <li>{@link ulb.controllers.combat.CombatController} — abstract generic base
 *       class parameterised on a concrete {@link ulb.views.combat.CombatView}
 *       subtype. Initialises the view on construction and exposes
 *       {@link ulb.controllers.combat.CombatController#handleCombatResult} to
 *       route the player to the victory or defeat screen after a combat session
 *       concludes.</li>
 *   <li>{@link ulb.controllers.combat.AutomaticCombatController} — drives a
 *       fully automated combat session via
 *       {@link
 * ulb.controllers.combat.AutomaticCombatController#runAutoCombat(ulb.models.trainer.AutoTrainer)}.
 *       Both the player's and the opponent's teams act randomly each turn;
 *       the entire session runs to completion in a single blocking call.</li>
 *   <li>{@link ulb.controllers.combat.ManualCombatController} — drives a
 *       player-controlled combat session via
 *       {@link
 * ulb.controllers.combat.ManualCombatController#runManualCombat(ulb.models.trainer.ManualTrainer)}.
 *       The player selects actions through the UI each turn while the opponent
 *       acts automatically. Full UI integration is currently a work in
 *       progress.</li>
 * </ul>
 *
 * <h2>Combat lifecycle</h2>
 * <ol>
 *   <li>The {@link ulb.controllers.MetaController} calls
 *       {@link ulb.controllers.MetaController#launchAutoCombat()} or
 *       {@link ulb.controllers.MetaController#launchManualCombat()}, which
 *       creates a player {@link ulb.models.trainer.Trainer} and delegates to
 *       the appropriate controller.</li>
 *   <li>The controller builds the opponent's
 *       {@link ulb.models.bugemon_team.BugemonTeam} by random sampling from the
 *       full pool of available Bugemons (same size as the player's team).</li>
 *   <li>The combat loop runs until one side is fully defeated, at which point
 *       {@link ulb.controllers.combat.CombatController#handleCombatResult} is
 *       called to navigate to
 *       {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY} or
 *       {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT}.</li>
 * </ol>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>Both concrete controllers currently run their combat loops synchronously
 *       on the JavaFX Application Thread. Very long sessions may therefore cause
 *       the UI to become unresponsive. A future refactor should move the loop to
 *       a background thread and update the view incrementally between turns.</li>
 *   <li>Manual action selection from the UI is not yet fully wired up in
 *       {@link ulb.controllers.combat.ManualCombatController}; the current
 *       implementation passes {@code null} as the action, which will trigger an
 *       {@link java.lang.IllegalArgumentException} at runtime.</li>
 *   <li>Each combat controller holds a typed reference to its
 *       {@link ulb.views.combat.CombatView} subclass (via the {@code View} type
 *       parameter inherited from {@link ulb.controllers.Controller}), eliminating
 *       the need for casts when accessing screen-specific view API.</li>
 * </ul>
 *
 * @see ulb.controllers
 * @see ulb.models.combat
 * @see ulb.views.combat
 * @see ulb.controllers.MetaController
 */
package ulb.controllers.combat;
