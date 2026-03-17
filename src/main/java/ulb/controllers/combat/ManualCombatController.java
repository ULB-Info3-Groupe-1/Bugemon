package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
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
    private Combat combat;
    private ManualTrainer player;
    private AutoTrainer opponent;

    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());
        this.view.setOnAttack(this::onAttack);
        this.view.setOnSwitch(this::onSwitch);
        this.view.setOnSurrender(this::onSurrender);
    }

    /** Initialises and starts a new manual combat session for the given player. */
    public void runManualCombat(ManualTrainer player) {
        this.player = player;
        this.opponent = createRandomOpponent(player);
        this.combat = new Combat(player, opponent);

        this.view.setModel(player, opponent, combat);
        this.view.refresh();
    }

    // ── Private callbacks (registered on the view) ───────────────────────────

    private void onAttack(Attack attack) {
        player.setHasSwitchedThisTurn(false);
        player.registerAttack(attack);
        handlePostTurn(combat.turn());
    }

    private void onSwitch(String bugemonId) {
        Bugemon target = player.getBugemonById(bugemonId);

        if (player.isForcedToSwitch()) {
            player.switchAfterKO(target);
            player.setForcedSwitch(false);
            view.refresh();
        } else {
            player.setHasSwitchedThisTurn(true);
            player.registerSwitch(target);
            handlePostTurn(combat.turn());
        }
    }

    private void handlePostTurn(TurnResult result) {
        if (combat.isFinished()) {
            handleCombatResult(combat.getWinner().get(), player);
        } else {
            if (result.allyIsKo()) {
                player.setForcedSwitch(true);
            }
            view.refresh();
        }
    }

    private void onSurrender() {
        player.registerForfeit();
        combat.turn();
        handleCombatResult(combat.getWinner().get(), player);
    }
}
