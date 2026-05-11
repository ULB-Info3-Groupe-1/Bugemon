package ulb.controllers.combat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatContext;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.combat.TurnResult;
import ulb.models.combat.TurnStep;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.TeamService;
import ulb.views.combat.CombatView;

/**
 * Abstract base controller for all combat screens. Manages the step-by-step iteration of a {@link TurnResult}: each
 * call to {@link #advanceStep()} resolves the current step (KO reactions, end-of-combat detection) then delegates to
 * {@link #showNextStep(TurnStep step, Runnable viewRefresh)} for the next animation and dialog. Subclasses implement
 * {@link #onStepsExhausted()} (what to do when a turn is fully displayed) and {@link #onCombatEnded(Trainer)}
 * (navigation on combat end).
 *
 * @param <V>
 *            the concrete {@link CombatView} subtype managed by this controller.
 */
public abstract class CombatController<V extends CombatView> extends Controller<V> implements CombatView.NextListener {
    private static final Logger LOG = LoggerFactory.getLogger(CombatController.class);

    protected final CombatAnimationController animationController;
    protected final TeamService teamService;
    protected final BugemonService bugemonService;
    protected final CombatService combatService;

    protected Combat combat;
    protected Trainer playerTrainer;
    protected Iterator<TurnStep> pendingSteps = Collections.emptyIterator();
    private Trainer pendingWinner = null;

    protected CombatController(MetaController metaController, TeamService teamService, BugemonService bugemonService,
            CombatService combatService, V view) {
        super(metaController, view);
        this.animationController = new CombatAnimationController(view);
        this.teamService = teamService;
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
        Runnable animationCallback = switch (step) {

            case TurnStep.TrainerKoStep(Trainer trainerKo) -> {
                this.processEndCombat(trainerKo, "Combat ended");
                yield () -> {
                };
            }

            case TurnStep.ForfeitStep(Trainer trainer) -> {
                this.processEndCombat(trainer, "Combat ended by forfeit");
                yield () -> {
                };
            }

            case TurnStep.BugemonKoStep(Trainer trainer) when !trainer.isDefeated() -> () -> {
                trainer.reactToKo();
                this.updateBugemonView(trainer);
                this.view.refreshMenuState();
            };

            case TurnStep.AttackStep s -> () -> {
                this.updateInfoForTrainer(this.getOpponentOf(s.attacker()));

                boolean selfHpEffect = s.getAttackEffects().stream()
                        .anyMatch(e -> e.target() == EffectTarget.THROWER && e instanceof EffectHeal);
                if (selfHpEffect) {
                    this.updateInfoForTrainer(s.attacker());
                }
            };

            case TurnStep.SwitchStep s -> () -> this.updateBugemonView(s.trainer());

            case TurnStep.ItemStep s -> () -> this.updateInfoForTrainer(s.trainer());

            default -> () -> {
            };
        };
        this.showNextStep(step, animationCallback);
    }

    private void processEndCombat(Trainer defeatedTrainer, String logPrefix) {
        Trainer winner = this.getOpponentOf(defeatedTrainer);
        LOG.info("{} – winner: {}", logPrefix, winner.getCurrentBugemonName());
        this.pendingWinner = winner;
    }

    private void updateBugemonView(Trainer trainer) {
        if (trainer == this.playerTrainer) {
            this.view.updateTrainerBugemon(trainer.getCurrentBugemon());
        } else {
            this.view.updateOpponentBugemon(trainer.getCurrentBugemon());
        }
    }

    private Trainer getOpponentOf(Trainer trainer) {
        return trainer == this.playerTrainer ? this.combat.getOpponentTrainer() : this.playerTrainer;
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
        if (won) {
            CombatContext ctx = new CombatContext(winner, this.getOpponentOf(winner));
            CombatXpDistributor xpDistributor = new CombatXpDistributor();
            List<LevelUp> generatedLevelUps = xpDistributor.distributeXp(ctx);
            winner.getParticipatingBugemons().forEach(this.bugemonService::saveBugemonState);
            if (!generatedLevelUps.isEmpty()) {
                this.metaController.receiveCombatResults(generatedLevelUps);
            }
        }
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
        // TODO: not used in ManualCombatController anymore
        return new AutoTrainer(
                CombatService.createRandomTeam(this.bugemonService.getAllDefaultBugemons(), playerTeamSize));
    }

    @Override
    public void onNext() {
        if (this.pendingWinner != null) {
            Trainer winner = this.pendingWinner;
            this.pendingWinner = null;
            this.onCombatEnded(winner);
            return;
        }
        this.advanceStep();
    }
}
