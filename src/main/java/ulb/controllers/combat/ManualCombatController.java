package ulb.controllers.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.Efficiency;
import ulb.common.dto.BugemonDTO;
import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.utils.Parser;
import ulb.views.combat.ManualCombatView;

/**
 * Controller responsible for the manual combat screen, where the player
 * actively selects an action each turn while the opponent acts automatically.
 *
 * <p>
 * {@code ManualCombatController} extends {@link CombatController} and drives a
 * {@link Combat} session between a {@link ManualTrainer} (the player) and an
 * {@link AutoTrainer} (the AI opponent). The controller translates every UI
 * event into a queued {@link ulb.models.trainer.TurnAction} on the
 * {@link ManualTrainer}, then triggers {@link Combat#turn()} to let the model
 * resolve the round. The resulting {@link TurnResult} is used to update the
 * view and check for end-of-combat conditions.
 * </p>
 *
 * <p>
 * Two distinct switch flows are managed:
 * <ul>
 * <li><strong>Tactical switch</strong> — the player voluntarily swaps their
 * active Bugemon as their turn action. The opponent still attacks this
 * turn. Controlled by {@link #switchActionUsed} to prevent switching
 * more than once per turn.</li>
 * <li><strong>Forced switch (post-KO)</strong> — the player's active Bugemon
 * fainted mid-turn and must be replaced before the next turn. No
 * additional combat turn is consumed. Controlled by
 * {@link #koSwitchFlag}.</li>
 * </ul>
 * <p>
 * Both flows converge on {@link #switchBugemon(String)}, which branches on
 * {@link #koSwitchFlag} to apply the correct behaviour.
 * </p>
 *
 * <p>
 * Once the combat ends, the inherited
 * {@link CombatController#handleCombatResult(Trainer, Trainer)} method
 * navigates to either
 * {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY}
 * or {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT} depending on
 * whether the player won.
 * </p>
 *
 * @see CombatController
 * @see Combat
 * @see ManualTrainer
 * @see AutoTrainer
 * @see ManualCombatView
 */
public class ManualCombatController extends CombatController<ManualCombatView> {
    /** The {@link Combat} instance managing the current session. */
    private Combat combat;

    /** The human-controlled trainer representing the player's side. */
    private ManualTrainer player;

    /** The AI-controlled trainer representing the opponent's side. */
    private AutoTrainer opponent;

    /**
     * Set to {@code true} when the player's active Bugemon fainted mid-turn
     * and a forced switch must be performed before the next turn can start.
     * Reset to {@code false} once the switch has been applied.
     */
    private boolean koSwitchFlag = false;

    /**
     * Set to {@code true} once the player has performed a voluntary (tactical)
     * switch this turn, preventing a second switch before the next attack.
     * Reset to {@code false} at the start of every attack turn.
     */
    private boolean switchActionUsed = false;

    // ── constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs a {@code ManualCombatController}, initialises its
     * {@link ManualCombatView}, and wires this controller as the view's event
     * handler.
     *
     * @param metaController the application-level {@link MetaController} used
     *                       for screen navigation and shared state; must not be
     *                       {@code null}.
     * @throws IOException if the {@link ManualCombatView} fails to load its
     *                     FXML resource.
     */
    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());
        this.view.setController(this);
    }

    // ── session lifecycle ─────────────────────────────────────────────────────

    /**
     * Initialises and starts a new manual combat session for the given player.
     *
     * <p>
     * An opponent team is generated randomly (same size as the player's team)
     * and a new {@link Combat} instance is created. Both flags
     * ({@link #koSwitchFlag} and {@link #switchActionUsed}) are implicitly
     * reset to {@code false} because a new controller state is established.
     * The view is brought to its initial combat layout and updated with the
     * starting state of both trainers.
     * </p>
     *
     * @param player the {@link ManualTrainer} representing the player's side;
     *               must not be {@code null} and must have a non-empty team.
     */
    public void runManualCombat(final ManualTrainer player) {
        this.player = player;
        this.opponent = new AutoTrainer(TeamFactory.createRandomTeam(
                Parser.getInstance().getBugemons(), player.getTeamSize()));
        this.combat = new Combat(player, opponent);
        this.view.showScreenCombatOpening();
        updateCombatView(player, opponent, null);
    }

    // ── UI callbacks (called by the view) ─────────────────────────────────────

    /**
     * Handles the player choosing to attack with the given {@link Attack}.
     *
     * <p>
     * Resets {@link #switchActionUsed} so the switch button becomes available
     * again after this attack turn. Queues an
     * {@link ulb.models.trainer.TurnAction.AttackAction} on the player, triggers
     * {@link Combat#turn()}, then delegates to
     * {@link #handleAfterTurn(TurnResult)}.
     * </p>
     *
     * @param attack the {@link Attack} selected by the player; must belong to
     *               the active Bugemon's move-set.
     */
    public void playerAttack(Attack attack) {
        switchActionUsed = false;
        player.registerAttack(attack);
        view.playTrainerAttackAnimation(() -> {
            TurnResult turnResult = combat.turn();
            handleAfterTurn(turnResult);
        });
    }

    /**
     * Handles the player choosing to forfeit the match.
     *
     * <p>
     * Queues a {@link ulb.models.trainer.TurnAction.ForfeitAction} on the
     * player, triggers {@link Combat#turn()}, then delegates to
     * {@link #handleAfterTurn(TurnResult)}. The combat model will instantly
     * defeat the player's entire team, causing
     * {@link Combat#getWinner()} to return the opponent.
     * </p>
     */
    public void surrender() {
        player.registerForfeit();
        TurnResult turnResult = combat.turn();
        handleAfterTurn(turnResult);
    }

    /**
     * Handles the player selecting a Bugemon to switch in, covering both the
     * tactical-switch and the forced post-KO-switch flows.
     *
     * <p>
     * <strong>Post-KO switch</strong> ({@link #koSwitchFlag} is {@code true}):
     * calls {@link ManualTrainer#switchAfterKO(Bugemon)} directly — no turn is
     * consumed and the opponent does not attack. Resets {@link #koSwitchFlag}
     * and restores the main action menu.
     * </p>
     *
     * <p>
     * <strong>Tactical switch</strong> ({@link #koSwitchFlag} is {@code false}):
     * sets {@link #switchActionUsed} to {@code true} to block a second switch
     * this turn, queues a {@link ulb.models.trainer.TurnAction.SwitchAction},
     * triggers {@link Combat#turn()} (the opponent still attacks), then
     * delegates to {@link #handleAfterTurn(TurnResult)}.
     * </p>
     *
     * @param bugemonId the unique identifier of the Bugemon to switch in;
     *                  must belong to the player's team and be alive.
     */
    public void switchBugemon(String bugemonId) {
        Bugemon target = player.getBugemonById(bugemonId);

        if (koSwitchFlag) {
            player.switchAfterKO(target);
            koSwitchFlag = false;
            view.updateTrainerBugemon(player.getCurrentBugemon());
            view.hideSwitchPanel();
            view.showMainActionMenu();
        } else {
            switchActionUsed = true;
            player.registerSwitch(target);
            TurnResult turnResult = combat.turn();
            handleAfterTurn(turnResult);
        }
    }

    /**
     * Opens the Bugemon team panel so the player can choose a Bugemon to
     * switch in, and hides all action menus.
     *
     * <p>
     * Used both for voluntary switches (from the main action menu) and for
     * forced post-KO switches. The actual switch is finalised when the player
     * clicks a Bugemon, which calls {@link #switchBugemon(String)}.
     * </p>
     */
    public void showSwitchMenu() {
        List<BugemonDTO> bugemonList = player.getTeam().stream().filter(Bugemon::isAlive).map(b -> (BugemonDTO) b)
                .toList();
        view.showSwitchMenu(bugemonList);
        view.hideAllActionMenus();
    }

    /**
     * Navigates the action menu to the attack selection screen, listing all
     * attacks available for the player's currently active Bugemon.
     */
    public void showAttackMenu() {
        view.hideSwitchPanel();
        view.showAttackMenu(player.getCurrentBugemonAttackList());
    }

    /**
     * Navigates the action menu back to the main action screen (Attack, Switch,
     * Surrender) and hides the team switch panel if it was visible.
     */
    public void showMainActionMenu() {
        view.hideSwitchPanel();
        view.showMainActionMenu();
    }

    // ── view queries ──────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the player is currently allowed to perform a
     * voluntary switch.
     *
     * <p>
     * A voluntary switch is forbidden when:
     * <ul>
     * <li>A forced post-KO switch is pending ({@link #koSwitchFlag} is
     * {@code true}).</li>
     * <li>A tactical switch has already been used this turn
     * ({@link #switchActionUsed} is {@code true}).</li>
     * </ul>
     * <p>
     * This method is called by {@link ulb.views.combat.MainActionMenu} to
     * conditionally enable the "Changer de Bugémon" button.
     * </p>
     *
     * @return {@code true} if a voluntary switch may be performed right now,
     *         {@code false} otherwise.
     */
    public boolean canSwitch() {
        return !koSwitchFlag && !switchActionUsed;
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Processes the result of a completed turn: updates the view, displays
     * attack effectiveness messages, checks for a winner, and opens the forced
     * switch menu if the player's active Bugemon was knocked out.
     *
     * <p>
     * The sequence of operations is:
     * <ol>
     * <li>Refresh both Bugemon info panels in the view.</li>
     * <li>Hide the switch panel and show the main action menu.</li>
     * <li>Display effectiveness dialogs for the first and (if present) second
     * hit of the turn.</li>
     * <li>If {@link Combat#getWinner()} is non-empty, delegate to
     * {@link CombatController#handleCombatResult(Trainer, Trainer)}.</li>
     * <li>If the combat is not finished but the player's active Bugemon
     * fainted, set {@link #koSwitchFlag} and open the switch menu.</li>
     * </ol>
     * </p>
     *
     * @param result the {@link TurnResult} returned by {@link Combat#turn()};
     *               must not be {@code null}.
     */
    private void handleAfterTurn(TurnResult result) {
        Runnable onAnimationFinished = () -> finalizeTurn(result);
        if (didTrainerAttack(result, opponent)) {
            view.playOpponentAttackAnimation(onAnimationFinished);
            return;
        }
        onAnimationFinished.run();
    }

    /**
     * Finalises the turn by updating the view, displaying dialogs, checking for
     * a winner, and opening the switch menu if needed.
     * 
     * @param result the {@link TurnResult} of the turn that just completed; must
     *               not be {@code null}.
     */
    private void finalizeTurn(TurnResult result) {
        view.updateTrainerBugemon(player.getCurrentBugemon());
        view.updateOpponentBugemon(opponent.getCurrentBugemon());
        view.hideSwitchPanel();
        view.showMainActionMenu();

        showTurnDialog(result);

        combat.getWinner().ifPresent(winner -> handleCombatResult(winner, player));

        if (!combat.isFinished() && result.allyIsKo()) {
            koSwitchFlag = true;
            showSwitchMenu();
        }
    }

    /**
     * Displays the combat dialog(s) for the given {@code result}.
     * 
     * @param result the {@link TurnResult} of the turn that just completed; must
     *               not be {@code null}.
     */
    private void displayTurnDialog(TurnResult result) {
        displayAttackResult(result.first(), result.second());
    }

    /**
     * Displays an effectiveness dialog for a single
     * {@link TurnResult.AttackResult}.
     *
     * <p>
     * Does nothing if the result does not represent an actual attack (i.e.
     * {@link TurnResult.AttackResult#wasAttack()} returns {@code false}).
     * Otherwise, formats the type-matchup message via
     * {@link CombatController#formatEfficiency(ulb.common.Efficiency)}
     * and passes it to
     * {@link ulb.views.combat.CombatView#showDialog(String, String)}.
     * </p>
     *
     * @param attackResult the {@link TurnResult.AttackResult} to display;
     *                     must not be {@code null}.
     */

    /**
     * Determines the type-matchup {@link ulb.common.Efficiency} of the given
     * {@link Attack} against the opponent's currently active Bugemon.
     *
     * <p>
     * Delegates to
     * {@link ulb.services.CombatService#compareBugemonType(ulb.models.bugemon.BugemonType,
     * ulb.models.bugemon.BugemonType)}, passing the attack's elemental type and
     * the opponent's active Bugemon type. The result can be used by the view to
     * annotate attack buttons with effectiveness indicators before a turn is
     * committed.
     * </p>
     *
     * @param attack the {@link Attack} whose type effectiveness is to be
     *               evaluated; must not be {@code null}.
     * @return the {@link ulb.common.Efficiency} representing how effective the
     *         attack's type is against the opponent's current Bugemon type;
     *         never {@code null}.
     */
    public Efficiency isAttackEfficient(Attack attack) {
        return CombatService.compareBugemonType(attack.type(), opponent.getCurrentBugemonType());
    }
}
