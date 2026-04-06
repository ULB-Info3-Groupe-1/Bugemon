package ulb.controllers.combat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.combat.TurnStep;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.LevelUpService;
import ulb.services.PlayerService;
import ulb.views.combat.CombatView;

/**
 * Abstract base controller for all combat screens. Manages the step-by-step iteration of a {@link TurnResult}: each
 * call to {@link #advanceStep()} resolves the current step (KO reactions, end-of-combat detection) then delegates to
 * {@link #showNextStep()} for the next animation and dialog. Subclasses implement {@link #onStepsExhausted()} (what to
 * do when a turn is fully displayed) and {@link #onCombatEnded(Trainer)} (navigation on combat end).
 *
 * @param <V>
 *            the concrete {@link CombatView} subtype managed by this controller.
 */
public abstract class CombatController<V extends CombatView> extends Controller<V> implements CombatView.NextListener {
    private static final Logger LOG = LoggerFactory.getLogger(CombatController.class);

    private Consumer<List<LevelUp>> onVictory;
    protected final CombatAnimationController animationController;
    protected final PlayerService playerService;
    protected final BugemonService bugemonService;
    protected boolean restoreHpAfterCombat;

    protected Combat combat;
    protected Trainer playerTrainer;
    protected Iterator<TurnStep> pendingSteps = Collections.emptyIterator();

    protected CombatController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService, V view) {
        super(metaController, view);
        this.animationController = new CombatAnimationController(view);
        this.playerService = playerService;
        this.bugemonService = bugemonService;

        this.view.setNextListener(this);
    }

    public void setOnVictory(Consumer<List<LevelUp>> onVictory) {
        this.onVictory = onVictory;
    }

    public abstract void startCombat(boolean shouldRestoreHp);

    // ── Step iteration ────────────────────────────────────────────────────────

    /** Runs one combat turn and starts iterating its steps. */
    protected void startTurn() {
        TurnResult result = this.combat.turn();
        this.pendingSteps = result.steps();
        this.advanceStep();
    }

    /**
     * Displays the next step: plays its animation then shows the dialog. Calls {@link #onStepsExhausted()} when all
     * steps of the current turn have been shown.
     */
    protected void showNextStep(TurnStep step) {
        this.animationController.playStepAnimation(step, this.playerTrainer, () -> {
            this.view.refresh();
            this.view.showStepDialog(step, this.playerTrainer);
        });
    }

    /**
     * Processes {@link #currentStep} and advances: detects end-of-combat steps, handles KO reactions for any
     * non-defeated trainer, then calls {@link #showNextStep()}. Subclass {@code onNext()} listener implementations
     * should delegate here.
     */
    protected void advanceStep() {
        if (!this.pendingSteps.hasNext()) {
            this.onStepsExhausted();
            return;
        }

        TurnStep step = this.pendingSteps.next();
        LOG.debug("Advancing step: {}", step);

        switch (step) {
            case TurnStep.TrainerKoStep koStep -> {
                Trainer winner = koStep.trainerKo() == this.playerTrainer ? this.combat.getOpponentTrainer()
                        : this.playerTrainer;
                LOG.info("Combat ended — winner: {}", winner.getCurrentBugemonName());
                this.onCombatEnded(winner);
                return;
            }

            case TurnStep.ForfeitStep forfeitStep -> {
                Trainer winner = forfeitStep.trainer() == this.playerTrainer ? this.combat.getOpponentTrainer()
                        : this.playerTrainer;
                LOG.info("Combat ended by forfeit — winner: {}", winner.getCurrentBugemonName());
                this.onCombatEnded(winner);
                return;
            }

            case TurnStep.BugemonKoStep koStep -> {
                if (!koStep.trainer().isDefeated()) {
                    koStep.trainer().reactToKo();
                    this.view.refresh();
                }
            }

            default -> {
            }
        }

        this.showNextStep(step);
    }

    /**
     * Called when all steps of the current turn have been displayed. Automatic combat starts the next turn immediately;
     * manual combat shows the post-turn menu.
     */
    protected abstract void onStepsExhausted();

    /**
     * Called when a {@link TurnStep.TrainerKoStep} or {@link TurnStep.ForfeitStep} is reached.
     *
     * @param winner
     *            the trainer who won the combat.
     */
    protected abstract void onCombatEnded(Trainer winner);

    // ── Shared utilities ──────────────────────────────────────────────────────

    /**
     * Creates a random opponent team sized to match the given player's team.
     *
     * @param playerTeamSize
     *            the size of the player's team, used to size the opponent's team.
     */
    protected AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(
                CombatService.createRandomTeam(this.bugemonService.getAllDefaultBugemons(), playerTeamSize));
    }

    /**
     * Distributes XP on victory and navigates to the correct outcome screen.
     *
     * @param winner
     *            the winning trainer.
     */
    protected void handleCombatResult(Trainer winner) {
        if (winner == this.playerTrainer) {
            List<LevelUp> levelUps = LevelUpService.distributeXpAndGetLevelUps(winner,
                    this.combat.getOpponentTrainer());
            this.onVictory.accept(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
        if (this.restoreHpAfterCombat) {
            this.playerService.restoreHpActiveTeam();
        }
    }

    @Override
    public void onNext() {
        this.advanceStep();
    }
}
