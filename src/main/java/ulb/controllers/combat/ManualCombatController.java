package ulb.controllers.combat;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.stage.Stage;

import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.PlayerService;
import ulb.views.ViewLoader;
import ulb.views.combat.ManualCombatView;

/**
 * Controller for the manual combat screen.
 *
 * <p>
 * Implements {@link ManualCombatView.Listener} to receive user combat actions. Each action mutates the model, then
 * calls {@code view.refresh()} so the view pulls the updated state itself. The controller never calls any show/hide
 * method on the view, and holds no knowledge of view layout.
 * </p>
 */
public class ManualCombatController extends CombatController<ManualCombatView> implements ManualCombatView.Listener {
    private Combat combat;
    private ManualTrainer playerTrainer;
    private Consumer<Boolean> onCombatFinished;

    /**
     * Constructs a {@code ManualCombatController} and wires itself as the view listener.
     *
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public ManualCombatController(MetaController metaController, PlayerService playerService) throws IOException {
        super(metaController, playerService, ViewLoader.load(ManualCombatView::new));
        this.view.setListener(this);
    }

    /** Initialises and starts a new manual combat session for the given player. */
    @Override
    public void startCombat(boolean shouldRestoreHp) {
        this.restoreHpAfterCombat = shouldRestoreHp;

        this.playerTrainer = new ManualTrainer(this.playerService.getActiveTeam(), this.playerService.getInventory());

        AutoTrainer opponentTrainer = createRandomOpponent(this.playerTrainer.getTeamSize());
        this.combat = new Combat(this.playerTrainer, opponentTrainer);

        this.view.setModel(this.playerTrainer, opponentTrainer, this.combat);
        this.view.refresh();
    }

    /**
     * Starts a manual combat session using an already prepared combat instance.
     *
     * @param newCombat
     *            combat model to drive from this controller.
     * @throws IllegalArgumentException
     *             if the ally trainer is not a ManualTrainer.
     */
    public void startCombat(Combat newCombat) {
        if (!(newCombat.getAllyTrainer() instanceof ManualTrainer manualAlly)) {
            throw new IllegalArgumentException("Manual combat requires a ManualTrainer as ally");
        }

        this.playerTrainer = manualAlly;
        this.combat = newCombat;
        Trainer opponentTrainer = this.combat.getAdversaryTrainer();

        this.view.setModel(this.playerTrainer, opponentTrainer, this.combat);
        this.view.refresh();
    }

    /**
     * Registers a callback invoked when the combat ends.
     *
     * @param callback
     *            receives {@code true} when the player wins, {@code false} otherwise.
     */
    public void setOnCombatFinished(Consumer<Boolean> callback) {
        this.onCombatFinished = callback;
    }

    /** Public bridge used by other controllers to display this combat controller. */
    public void display(Stage stage) {
        this.show(stage);
    }

    // ── ManualCombatView.Listener ─────────────────────────────────────────────

    /**
     * Registers the chosen attack, advances the turn, then handles the result.
     *
     * @param attack
     *            the attack chosen by the user.
     */
    @Override
    public void onAttack(Attack attack) {
        this.playerTrainer.registerAttack(attack);
        this.handleAnimatedPostTurn(this.combat.turn());
    }

    /**
     * Handles a switch request. If a forced post-KO switch is pending the switch is applied immediately without
     * consuming a turn; otherwise a normal switch action is registered and the turn is advanced.
     */
    @Override
    public void onSwitch(Bugemon target) {
        if (this.playerTrainer.isForcedToSwitch()) {
            this.playerTrainer.switchAfterKO(target);
            this.playerTrainer.setForcedSwitch(false);
            this.view.refresh();
        } else {
            this.playerTrainer.setHasSwitchedThisTurn(true);
            this.playerTrainer.registerSwitch(target);
            this.handleAnimatedPostTurn(this.combat.turn());
        }
    }

    /** Registers a forfeit action, resolves the turn, and navigates to the defeat screen. */
    @Override
    public void onSurrender() {
        this.playerTrainer.registerForfeit();
        this.combat.turn();
        boolean playerWon = this.combat.getWinner().orElseThrow() == this.playerTrainer;
        if (this.onCombatFinished != null) {
            this.onCombatFinished.accept(playerWon);
            return;
        }
        this.handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
    }

    @Override
    public void onItemSelected(Item item) {
        this.playerTrainer.registerUseItem(item);
        this.combat.turn();
        this.view.refresh();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void handleAnimatedPostTurn(TurnResult result) {
        this.playTurnAnimations(result, this.playerTrainer, () -> this.handlePostTurn(result));
    }

    /**
     * Navigates to the outcome screen if combat ended, or refreshes the view.
     *
     * @param result
     *            the turn result used to check for KO switches and combat end.
     */
    private void handlePostTurn(TurnResult result) {
        if (this.combat.isFinished()) {
            boolean playerWon = this.combat.getWinner().orElseThrow() == this.playerTrainer;
            if (this.onCombatFinished != null) {
                this.onCombatFinished.accept(playerWon);
                return;
            }
            this.handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
        } else {
            if (result.allyIsKo()) {
                this.playerTrainer.setForcedSwitch(true);
            }
            this.playerTrainer.setHasSwitchedThisTurn(false);
            this.view.refresh();
        }
    }
}
