package ulb.controllers.combat;

import java.util.Collections;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.combat.TurnStep;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;
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

    protected final CombatAnimationController animationController;
    protected final PlayerService playerService;
    protected final BugemonService bugemonService;
    protected final CombatService combatService;

    protected Combat combat;
    protected Trainer playerTrainer;
    protected Iterator<TurnStep> pendingSteps = Collections.emptyIterator();

    protected CombatController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService, CombatService combatService, V view) {
        super(metaController, view);
        this.animationController = new CombatAnimationController(view);
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.combatService = combatService;

        this.view.setNextListener(this);
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
     * Displays the next step: plays its animation, then runs the targeted {@code viewRefresh} callback, then shows the
     * dialog. The callback updates only the UI elements affected by this specific step.
     */
    protected void showNextStep(TurnStep step, Runnable viewRefresh) {
        this.view.lockNextButton();
        this.animationController.playStepAnimation(step, this.playerTrainer, () -> {
            viewRefresh.run();
            this.view.showStepDialog(step, this.playerTrainer);
        });
    }

    /**
     * Processes the next pending step. Delegates entirely to {@link #handleStep(TurnStep)}.
     */
    protected void advanceStep() {
        if (!this.pendingSteps.hasNext()) {
            this.onStepsExhausted();
            return;
        }
        TurnStep step = this.pendingSteps.next();
        LOG.debug("Advancing step: {}", step);
        this.handleStep(step);
    }

    /**
     * Single dispatch point for all step types. Each branch owns both the model reaction and the targeted
     * post-animation view update, keeping them in sync.
     */
    private void handleStep(TurnStep step) {
        switch (step) {
            case TurnStep.TrainerKoStep(Trainer trainerKo) -> {
                Trainer winner = trainerKo == this.playerTrainer ? this.combat.getOpponentTrainer()
                        : this.playerTrainer;
                LOG.info("Combat ended – winner: {}", winner.getCurrentBugemonName());
                this.onCombatEnded(winner);
            }

            case TurnStep.ForfeitStep(Trainer trainer) -> {
                Trainer winner = trainer == this.playerTrainer ? this.combat.getOpponentTrainer() : this.playerTrainer;
                LOG.info("Combat ended by forfeit – winner: {}", winner.getCurrentBugemonName());
                this.onCombatEnded(winner);
            }

            // reactToKo() and view update are deferred into the animation callback so the
            // death animation plays on the dead Bugemon. After the fade-out, only the KO'd
            // side updates: makeReappear() fades the new Bugemon in from opacity 0.
            // refreshMenuState() handles the forced-switch menu without touching the other
            // side.
            case TurnStep.BugemonKoStep(Trainer trainer) when !trainer.isDefeated() -> this.showNextStep(step, () -> {
                trainer.reactToKo();
                if (trainer == this.playerTrainer) {
                    this.view.updateTrainerBugemon(trainer.getCurrentBugemon());
                } else {
                    this.view.updateOpponentBugemon(trainer.getCurrentBugemon());
                }
                this.view.refreshMenuState();
            });

            case TurnStep.AttackStep s -> this.showNextStep(step, () -> {
                Trainer defender = s.attacker() == this.playerTrainer ? this.combat.getOpponentTrainer()
                        : this.playerTrainer;
                this.updateInfoForTrainer(defender);
                boolean selfHpEffect = s.getAttackEffects().stream()
                        .anyMatch(e -> e.target() == EffectTarget.THROWER && e instanceof EffectHeal);
                if (selfHpEffect) {
                    this.updateInfoForTrainer(s.attacker());
                }
            });

            case TurnStep.SwitchStep s -> this.showNextStep(step, () -> {
                if (s.trainer() == this.playerTrainer) {
                    this.view.updateTrainerBugemon(s.trainer().getCurrentBugemon());
                } else {
                    this.view.updateOpponentBugemon(s.trainer().getCurrentBugemon());
                }
            });

            case TurnStep.ItemStep s -> this.showNextStep(step, () -> this.updateInfoForTrainer(s.trainer()));

            default -> this.showNextStep(step, () -> {
            });
        }
    }

    private void updateInfoForTrainer(Trainer trainer) {
        if (trainer == this.playerTrainer) {
            this.view.updateTrainerInfo(trainer.getCurrentBugemon());
        } else {
            this.view.updateOpponentInfo(trainer.getCurrentBugemon());
        }
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
    protected void onCombatEnded(Trainer winner) {
        boolean won = winner == this.playerTrainer;
        this.metaController.onCombatFinished(won);
    }

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

    @Override
    public void onNext() {
        this.advanceStep();
    }
}
