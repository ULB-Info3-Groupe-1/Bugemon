package ulb.controllers.combat;

import java.util.Collections;

import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.combat.Combat;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.TeamService;
import ulb.views.ViewLoader;
import ulb.views.combat.ManualCombatView;

/**
 * Controller for the manual combat screen. Implements {@link ManualCombatView.Listener} to receive player combat
 * actions. Each action mutates the model, then iterates the resulting {@link ulb.models.combat.TurnResult} steps one by
 * one via the dialog zone. The controller never calls any show/hide method on the view directly, and holds no knowledge
 * of view layout.
 */
public class ManualCombatController extends CombatController<ManualCombatView> implements ManualCombatView.Listener {
    private ManualTrainer manualPlayerTrainer;
    private final InventoryService inventoryService;

    /**
     * Constructs a {@code ManualCombatController} and wires itself as the view listener.
     *
     */
    public ManualCombatController(MetaController metaController, TeamService teamService, BugemonService bugemonService,
            InventoryService inventoryService, CombatFactory combatFactory) {
        super(metaController, teamService, bugemonService, combatFactory, ViewLoader.load(ManualCombatView::new));
        this.inventoryService = inventoryService;
        this.view.setListener(this);
    }

    /** Initialises and starts a new manual combat session for the given player. */
    @Override
    public void startCombat() {
        ManualTrainer player = new ManualTrainer(this.teamService.getRequiredActiveTeam(), this.inventoryService);
        AutoTrainer opponentTrainer = createRandomOpponent(player.getTeamSize());
        Combat combat = this.combatFactory.create(player, opponentTrainer);
        this.startCombat(combat);
    }

    /**
     * Starts a manual combat session using an already prepared combat instance.
     *
     * @param newCombat
     *            the combat to drive from this controller.
     * @throws IllegalArgumentException
     *             if the player trainer is not a ManualTrainer.
     */
    public void startCombat(Combat newCombat) {
        if (!(newCombat.getPlayerTrainer() instanceof ManualTrainer playerManualTrainer)) {
            throw new IllegalArgumentException("Manual combat requires a ManualTrainer as ally");
        }

        this.manualPlayerTrainer = playerManualTrainer;
        this.playerTrainer = this.manualPlayerTrainer;
        this.combat = newCombat;
        Trainer opponentTrainer = this.combat.getOpponentTrainer();

        this.view.setModel(this.manualPlayerTrainer, opponentTrainer);
        this.pendingSteps = Collections.emptyIterator();
        this.view.hideDialog();
        this.view.refresh();
    }

    // ── ManualCombatView.Listener ─────────────────────────────────────────────

    @Override
    public void onAttack(Attack attack) {
        this.manualPlayerTrainer.registerAttack(attack);
        this.startTurn();
    }

    /**
     * Handles a switch request. If a forced post-KO switch is pending the switch is applied immediately without
     * consuming a turn; otherwise a normal switch action is registered and the turn is advanced.
     */
    @Override
    public void onSwitch(Bugemon target) {
        if (this.manualPlayerTrainer.isForcedToSwitch()) {
            this.manualPlayerTrainer.switchAfterKO(target);
            this.manualPlayerTrainer.setForcedSwitch(false);
            this.view.updateTrainerBugemon(this.manualPlayerTrainer.getCurrentBugemon());
            this.view.refreshMenuState();
        } else {
            this.manualPlayerTrainer.setHasSwitchedThisTurn(true);
            this.manualPlayerTrainer.registerSwitch(target);
            this.startTurn();
        }
    }

    /** Registers a forfeit action, resolves the turn, and navigates to the outcome screen immediately. */
    @Override
    public void onForfeit() {
        this.manualPlayerTrainer.registerForfeit();
        this.startTurn();
    }

    @Override
    public void onItemSelected(Item item) {
        this.manualPlayerTrainer.registerUseItem(item);
        this.startTurn();
    }

    // ── CombatController hooks ────────────────────────────────────────────────

    @Override
    protected void onCombatEnded(ulb.models.trainer.Trainer winner) {
        this.inventoryService.saveInventory();
        super.onCombatEnded(winner);
    }

    protected void onStepsExhausted() {
        if (!this.manualPlayerTrainer.isCurrentBugemonAlive()) {
            this.manualPlayerTrainer.setForcedSwitch(true);
        }
        this.manualPlayerTrainer.setHasSwitchedThisTurn(false);
        this.view.hideDialog();
        this.view.refreshMenuState();
    }
}
