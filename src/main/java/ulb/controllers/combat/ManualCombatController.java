package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.player.Player;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
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
    private final Player player;

    private Combat combat;
    private ManualTrainer playerTrainer;
    private AutoTrainer opponentTrainer;

    /**
     * Constructs a {@code ManualCombatController}, initialises its {@link ManualCombatView},
     * and registers the attack, switch, and surrender callbacks.
     *
     * @param metaController the application-level controller used for navigation.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public ManualCombatController(MetaController metaController, Player player) throws IOException {
        super(metaController, new ManualCombatView());

        this.player = player;

        this.view.setOnAttack(this::onAttack);
        this.view.setOnSwitch(this::onSwitch);
        this.view.setOnSurrender(this::onSurrender);
    }

    /** Initialises and starts a new manual combat session for the given player. */
    @Override
    public void startCombat() {
        System.out.println("Starting manual combat with player team: " + player.getActiveTeam());
        this.playerTrainer = new ManualTrainer(player.getActiveTeam());
        this.opponentTrainer = createRandomOpponent(this.playerTrainer.getTeamSize());
        this.combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(playerTrainer, opponentTrainer, this.combat);
        this.view.refresh();
    }

    // ── Private callbacks (registered on the view) ───────────────────────────

    /** Registers the chosen attack, advances the turn, then handles the result. */
    private void onAttack(Attack attack) {
        this.playerTrainer.setHasSwitchedThisTurn(false);
        this.playerTrainer.registerAttack(attack);
        handlePostTurn(this.combat.turn());
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
            handlePostTurn(this.combat.turn());
        }
    }

    /** Navigates to the outcome screen if combat ended, or refreshes the view. */
    private void handlePostTurn(TurnResult result) {
        if (this.combat.isFinished()) {
            handleCombatResult(this.combat.getWinner().get(), this.playerTrainer);
        } else {
            if (result.allyIsKo()) {
                this.playerTrainer.setForcedSwitch(true);
            }
            this.view.refresh();
        }
    }

    /** Registers a forfeit action, resolves the turn, and navigates to the defeat screen. */
    private void onSurrender() {
        this.playerTrainer.registerForfeit();
        this.combat.turn();
        handleCombatResult(this.combat.getWinner().get(), this.playerTrainer);
    }
}
