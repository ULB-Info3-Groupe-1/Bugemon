package ulb.controllers.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.ManualCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.ManualTrainer.TAction;
import ulb.models.trainer.Trainer;
import ulb.utils.Parser;
import ulb.views.combat.ManualCombatView;

/**
 * Controller responsible for the manual combat screen, where the player
 * actively selects actions each turn while the opponent acts automatically.
 *
 * <p>
 * {@code ManualCombatController} extends {@link CombatController} and drives a
 * {@link ManualCombat} session. The combat loop runs until one side has no
 * remaining alive {@link ulb.models.bugemon.Bugemon}s, at which point the
 * inherited
 * {@link CombatController#handleCombatResult(Trainer, Trainer)}
 * method navigates to either the
 * {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY} or the
 * {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT} screen depending
 * on whether the player won.
 * </p>
 *
 * <p>
 * The opponent team is constructed automatically by randomly sampling
 * {@link BugemonTeam#createRandomTeam(java.util.List, int)} from the full pool
 * of available Bugemons, using the same team size as the player's team.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> action selection from the UI is not yet implemented.
 * {@link ManualCombat#turn(ManualTrainer.TAction)} is currently called with
 * {@code null}, which will cause an {@link IllegalArgumentException}. This is a
 * known stub awaiting integration with the view layer.
 * </p>
 *
 * @see CombatController
 * @see ManualCombat
 * @see ManualTrainer
 * @see AutoTrainer
 * @see ManualCombatView
 */
public class ManualCombatController extends CombatController<ManualCombatView> {
    private ManualCombat combat;
    private ManualTrainer player;
    private AutoTrainer opponent;

    /**
     * Constructs a {@code ManualCombatController}, initialises its
     * {@link ManualCombatView}, and registers this controller as the view's
     * event handler.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its
     * scene graph is ready before the controller is used for the first time.
     * The parent constructor ({@link CombatController}) also pre-populates the
     * view with placeholder {@link ulb.models.bugemon.Bugemon}s and calls
     * {@link ulb.views.combat.CombatView#initCombatMode()}.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and shared state access; must not be
     *                       {@code null}.
     * @throws IOException if the {@link ManualCombatView} fails to load its FXML
     *                     resource.
     */
    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());
        this.view.setOnShowAttackMenu(this::showAttackMenu);
        this.view.setOnShowSwitchMenu(this::showSwitchMenu);
        this.view.setOnSurrender(this::surrender);
        this.view.setOnBackToMainActionMenu(this::showMainActionMenu);
        this.view.setOnAttackSelected(this::playerAttack);
        this.view.setOnSwitchBugemon(this::switchBugemon);
    }

    /**
     * Runs a complete manual combat session from start to finish using the given
     * player {@link ManualTrainer}.
     *
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Builds the opponent's {@link BugemonTeam} by randomly sampling from
     *       the pool of all available Bugemons (same size as the player's team)
     *       via {@link BugemonTeam#createRandomTeam(java.util.List, int)}.</li>
     *   <li>Creates a {@link ManualCombat} between the player
     *       ({@link ManualTrainer}) and the opponent ({@link AutoTrainer}).</li>
     *   <li>Loops until {@link ManualCombat#turn(ManualTrainer.TAction)} returns
     *       a non-{@code null} {@link Trainer} (the winner), passing the
     *       player-selected action each iteration.</li>
     *   <li>Delegates to {@link #handleCombatResult(Trainer, Trainer)} to
     *       navigate to the appropriate outcome screen.</li>
     * </ol>
     * </p>
     *
     * <p>
     * <strong>Note:</strong> action selection from the UI is not yet wired up.
     * The action is currently passed as {@code null}, which will cause an
     * {@link IllegalArgumentException} inside {@link ManualCombat#turn}. This
     * method is therefore a stub pending full integration with the view layer.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> this method runs the entire combat loop
     * synchronously on the calling thread. Because it is currently invoked on
     * the JavaFX Application Thread, long-running combats may cause the UI to
     * become unresponsive. A future refactor should move the loop to a background
     * thread and update the view incrementally.
     * </p>
     *
     * @param player the {@link ManualTrainer} representing the player's side;
     *               must not be {@code null} and must have a non-empty team.
     */
    public void runManualCombat(final ManualTrainer player) {
        this.player = player;
        this.opponent = new AutoTrainer(TeamFactory.createRandomTeam(
                Parser.getInstance().getBugemons(), player.getTeamSize()));
        this.combat = new ManualCombat(player, opponent);
        this.view.showScreenDebutCombat();
        updateCombatView(player, opponent, null);
    }

    /**
     * Handle the player's attack action
     * @param attack the attack selected by the player
     */
    public void playerAttack(Attack attack) {
        try {
            Bugemon enemyBugemon = this.opponent.getCurrentBugemon().clone();

            this.player.selectAttack(attack);
            this.player.selectAction(TAction.ATTACK);
            Trainer winner = this.combat.turn();
            // this.view.showDialog(attack.getName().toString(), null);
            if (enemyBugemon.getId() != this.opponent.getCurrentBugemon().getId()) {
                this.view.hideDialog();
                attack = null;
            }
            handlePlayerTurn(winner, attack);

        } catch (CloneNotSupportedException e) {
            System.err.println("Error cloning opponent's Bugemon for attack display: "
                               + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle the player's switch action
     * @param bugemonId the id of the Bugemon to switch to
     */
    public void switchBugemon(String bugemonId) {
        this.player.setSelectedBugemon(this.player.getBugemonById(bugemonId));
        this.player.selectAction(TAction.SWITCH);
        Trainer winner = this.combat.turn();
        handlePlayerTurn(winner, null);
    }

    /**
     * Show the switch menu to the player to select a Bugemon to switch to
     */
    public void showSwitchMenu() {
        // TODO: change that
        List<BugemonDTO> bugemonList = this.player.getTeam()
                                               .stream()
                                               .filter(Bugemon::isAlive)
                                               .map(b -> (BugemonDTO)b)
                                               .toList();
        this.view.showSwitchMenu(bugemonList);
        this.view.hideAllActionMenus();
    }

    /**
     * Handle the player's surrender action
     */
    public void surrender() {
        this.player.selectAction(TAction.FORFEIT);
        Trainer winner = this.combat.turn();
        handlePlayerTurn(winner, null);
    }

    /**
     * Handle the end of the player's turn, update the view and check if there is a winner
     */
    private void handlePlayerTurn(Trainer winner, Attack attack) {
        updateCombatView(this.player, this.opponent, attack);
        if (winner != null) {
            handleCombatResult(winner, player);
        }
        if (!this.player.isCurrentBugemonAlive()) {
            showSwitchMenu();
        }
    }

    /**
     * Show the attack menu to the player with the list of available attacks
     */
    public void showAttackMenu() {
        this.view.hideSwitchPanel();
        this.view.showAttackMenu(this.player.getCurrentBugemonAttackList(),
                                 this.opponent.getCurrentBugemonType());
    }

    /**
     * Show the main action menu to the player (Attack, Switch, Surrender)
     */
    public void showMainActionMenu() {
        this.view.hideSwitchPanel();
        this.view.showMainActionMenu();
    }

    public BugemonType getOpponentBugemonType() {
        return this.opponent.getCurrentBugemonType();
    }
}