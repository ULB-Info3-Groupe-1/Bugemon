package ulb.controllers.combat;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.stage.Stage;

import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.bugemon.Item;
import ulb.models.trainer.Trainer;
import ulb.services.PlayerService;
import ulb.views.combat.ManualCombatView;

/**
 * Controller for the manual combat screen.
 *
 * <p>
 * Registers action callbacks on the {@link ManualCombatView} at construction
 * time. Each callback mutates the model, then calls {@code view.refresh()} so
 * the view pulls the updated state itself. The controller never calls any
 * show/hide method on the view, and holds no knowledge of view layout.
 * </p>
 */
public class ManualCombatController extends CombatController<ManualCombatView> {
    private Combat combat;
    private ManualTrainer playerTrainer;
    private Trainer opponentTrainer;
    private Consumer<Boolean> onCombatFinished;

    /**
     * Constructs a {@code ManualCombatController}, initialises its
     * {@link ManualCombatView},
     * and registers the attack, switch, and surrender callbacks.
     *
     * @param metaController the application-level controller used for navigation.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public ManualCombatController(MetaController metaController, PlayerService playerService)
            throws IOException {
        super(metaController, playerService, new ManualCombatView());

        this.view.setOnAttack(this::onAttack);
        this.view.setOnSwitch(this::onSwitch);
        this.view.setOnSurrender(this::onSurrender);
        this.view.setOnItemSelected(this::onItemSelected);

    }

    /** Initialises and starts a new manual combat session for the given player. */
    @Override
    public void startCombat(boolean restoreHpAfterCombat) {
        this.restoreHpAfterCombat = restoreHpAfterCombat;

        this.playerTrainer = new ManualTrainer(this.playerService.getActiveTeam(),
                                               this.playerService.getInventory());
        AutoTrainer opponentTrainer = createRandomOpponent(this.playerTrainer.getTeamSize());
        this.combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(playerTrainer, opponentTrainer, this.combat);
        this.view.refresh();
    }

    /**
     * Starts a manual combat session using an already prepared combat instance.
     *
     * @param combat combat model to drive from this controller.
     * @throws IllegalArgumentException if the ally trainer is not a ManualTrainer.
     */
    public void startCombat(Combat combat) {
        if (!(combat.getAllyTrainer() instanceof ManualTrainer manualAlly)) {
            throw new IllegalArgumentException("Manual combat requires a ManualTrainer as ally");
        }

        this.playerTrainer = manualAlly;
        this.opponentTrainer = combat.getAdversaryTrainer();
        this.combat = combat;

        this.view.setModel(playerTrainer, opponentTrainer, this.combat);
        this.view.refresh();
    }

    /**
     * Registers a callback invoked when the combat ends.
     *
     * @param callback receives true when the player wins, false otherwise.
     */
    public void setOnCombatFinished(Consumer<Boolean> callback) {
        this.onCombatFinished = callback;
    }

    /**
     * Public bridge used by other controllers to display this combat controller.
     */
    public void display(Stage stage) {
        this.show(stage);
    }

    // ── Private callbacks (registered on the view) ───────────────────────────

    /**
     * Registers the chosen attack, advances the turn, then handles the result.
     *
     * @param attack the attack chosen by the user, registered on the player trainer
     *               to be executed in the next turn.
     */
    private void onAttack(Attack attack) {
        this.playerTrainer.registerAttack(attack);
        handleAnimatedPostTurn(this.combat.turn());
    }

    /**
     * Handles a switch request. If a forced post-KO switch is pending the
     * switch is applied immediately without consuming a turn; otherwise a
     * normal switch action is registered and the turn is advanced.
     */
    private void onSwitch(Bugemon target) {
        if (this.playerTrainer.isForcedToSwitch()) {
            this.playerTrainer.switchAfterKO(target);
            this.playerTrainer.setForcedSwitch(false);
            view.refresh();
        } else {
            this.playerTrainer.setHasSwitchedThisTurn(true);
            this.playerTrainer.registerSwitch(target);
            handleAnimatedPostTurn(this.combat.turn());
        }
    }

    private void handleAnimatedPostTurn(TurnResult result) {
        playTurnAnimations(result, this.playerTrainer, () -> handlePostTurn(result));
    }

    /**
     * Navigates to the outcome screen if combat ended, or refreshes the view.
     *
     * @param result the turn result to check for KO switches and to determine if
     *               the combat has ended.
     *
     */
    private void handlePostTurn(TurnResult result) {
        if (this.combat.isFinished()) {
            boolean playerWon = this.combat.getWinner().orElseThrow() == this.playerTrainer;
            if (this.onCombatFinished != null) {
                this.onCombatFinished.accept(playerWon);
                return;
            }
            handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
        } else {
            if (result.allyIsKo()) {
                this.playerTrainer.setForcedSwitch(true);
            }
            this.playerTrainer.setHasSwitchedThisTurn(false);
            this.view.refresh();
        }
    }

    /**
     * Registers a forfeit action, resolves the turn, and navigates to the defeat
     * screen.
     */
    private void onSurrender() {
        this.playerTrainer.registerForfeit();
        this.combat.turn();
        boolean playerWon = this.combat.getWinner().orElseThrow() == this.playerTrainer;
        if (this.onCombatFinished != null) {
            this.onCombatFinished.accept(playerWon);
            return;
        }
        handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
    }

    private void onItemSelected(Item item) {
        this.playerTrainer.registerUseItem(item);
        this.combat.turn();
        this.view.refresh();
    }
}
