package ulb.models.combat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.Effect;
import ulb.models.trainer.AITrainer;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.models.trainer.TurnAction;
import ulb.services.CombatService;

/**
 * Orchestrates a turn-based combat between two {@link Trainer}s. Each call to {@link #turn()} asks both trainers for
 * their {@link TurnAction} once, applies passive actions (switches, items), then resolves attacks in initiative order.
 * The resulting {@link TurnResult} lists every {@link TurnStep} in order; a {@link TurnStep.TrainerKoStep} signals the
 * end of combat and identifies the loser.
 */
public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);

    private final Trainer playerTrainer;
    private final Trainer opponentTrainer;

    private final EndOfCombatAction endOfCombatAction;
    private final ICombatXpDistributor xpDistributor;

    private TurnResult turnResult;

    private boolean isCompleted = false;

    public Combat(ICombatXpDistributor xpDistributor, Trainer playerTrainer, Trainer opponentTrainer) {
        this(xpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.NO_OP);
    }

    public Combat(ICombatXpDistributor xpDistributor, Trainer playerTrainer, Trainer opponentTrainer,
            EndOfCombatAction endOfCombatAction) {
        this.playerTrainer = playerTrainer;
        this.opponentTrainer = opponentTrainer;
        this.endOfCombatAction = endOfCombatAction;
        this.xpDistributor = xpDistributor;
        LOG.info("Combat started — player: {} vs opponent: {}", playerTrainer.getCurrentBugemonName(),
                opponentTrainer.getCurrentBugemonName());
    }

    /**
     * Resolves one full round and returns a {@link TurnResult} describing every step. Actions are fetched exactly once
     * per trainer. Sequence: mark participation → get actions → handle forfeit → apply passive actions → resolve
     * attacks in initiative order → update trainer status.
     */
    public TurnResult turn() {
        this.turnResult = new TurnResult();
        LOG.debug("New turn — player: {} ({}hp) vs opponent: {} ({}hp)", this.playerTrainer.getCurrentBugemonName(),
                this.playerTrainer.getCurrentBugemonHp(), this.opponentTrainer.getCurrentBugemonName(),
                this.opponentTrainer.getCurrentBugemonHp());

        this.syncOpponentAiState();

        this.markParticipation();

        TurnAction playerAction = this.playerTrainer.getAction();
        TurnAction opponentAction = this.opponentTrainer.getAction();

        if (!this.resolveForfeit(playerAction, opponentAction)) {
            this.resolveTurn(playerAction, opponentAction);
        }

        this.endTurn();

        return this.turnResult;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    private void markParticipation() {
        this.playerTrainer.markCurrentBugemonParticipation();
        this.opponentTrainer.markCurrentBugemonParticipation();
    }

    private boolean resolveForfeit(TurnAction playerAction, TurnAction opponentAction) {
        boolean forfeit = false; // Both trainers can forfeit at the same time

        if (playerAction instanceof TurnAction.ForfeitAction) {
            this.handleForfeit(this.playerTrainer);
            this.turnResult.addStep(new TurnStep.ForfeitStep(this.playerTrainer));
            forfeit = true;
        }
        if (opponentAction instanceof TurnAction.ForfeitAction) {
            this.handleForfeit(this.opponentTrainer);
            this.turnResult.addStep(new TurnStep.ForfeitStep(this.opponentTrainer));
            forfeit = true;
        }
        return forfeit;
    }

    private void resolveTurn(TurnAction playerAction, TurnAction opponentAction) {
        this.resolveItem(playerAction, opponentAction);
        this.resolveSwitch(playerAction, opponentAction);
        this.resolveAttack(playerAction, opponentAction);
    }

    private Trainer getWinner() {
        if (!this.isCompleted) {
            throw new IllegalStateException("cannot get the winner because the combat is not completed yet");
        }

        return this.playerTrainer.isDefeated() ? this.opponentTrainer : this.playerTrainer;
    }

    private void endTurn() {
        this.updateTrainerStatus(this.playerTrainer);
        this.updateTrainerStatus(this.opponentTrainer);

        if (this.isCompleted) {
            this.endOfCombatAction.execute(new CombatContext(this.playerTrainer, this.opponentTrainer));

            Trainer winner = this.getWinner();
            Trainer loser = this.getOtherTrainer(winner);

            CombatContext combatCtx = new CombatContext(winner, loser);

            this.xpDistributor.distributeXp(combatCtx);
        }
    }

    private void resolveItem(TurnAction playerAction, TurnAction opponentAction) {
        if (playerAction instanceof TurnAction.UseItemAction(Item useItemAction)) {
            this.handleItem(this.playerTrainer, useItemAction);
            this.turnResult.addStep(new TurnStep.ItemStep(this.playerTrainer, useItemAction));
        }
        if (opponentAction instanceof TurnAction.UseItemAction(Item useItemAction)) {
            this.handleItem(this.opponentTrainer, useItemAction);
            this.turnResult.addStep(new TurnStep.ItemStep(this.opponentTrainer, useItemAction));
        }
    }

    private void resolveSwitch(TurnAction playerAction, TurnAction opponentAction) {
        if (playerAction instanceof TurnAction.SwitchAction(Bugemon target)) {
            this.handleSwitch(this.playerTrainer, target);
            this.turnResult.addStep(new TurnStep.SwitchStep(this.playerTrainer, target));
        }
        if (opponentAction instanceof TurnAction.SwitchAction(Bugemon target)) {
            this.handleSwitch(this.opponentTrainer, target);
            this.turnResult.addStep(new TurnStep.SwitchStep(this.opponentTrainer, target));
        }
    }

    private void resolveAttack(TurnAction playerAction, TurnAction opponentAction) {
        // Automatic combat does not consider initiative
        if (this.playerTrainer instanceof AutoTrainer) {
            this.resolveAutoAttack(playerAction, opponentAction);
            return;
        }

        if (playerAction instanceof TurnAction.AttackAction(Attack playerAttack)) {
            if (opponentAction instanceof TurnAction.AttackAction(Attack opponentAttack)) {
                this.resolveDualAttack(playerAttack, opponentAttack);
                return;
            }
            // Only the player trainer attacked
            this.resolveSoloAttack(this.playerTrainer, playerAttack, this.opponentTrainer);
            return;
        }
        // Only the opponent trainer attacked
        if (opponentAction instanceof TurnAction.AttackAction(Attack opponentAttack)) {
            this.resolveSoloAttack(this.opponentTrainer, opponentAttack, this.playerTrainer);
        }
    }

    private void handleForfeit(Trainer trainer) {
        LOG.info("{} forfeited", trainer.getCurrentBugemonName());
        trainer.killTeam();
    }

    private void updateTrainerStatus(Trainer trainer) {
        if (!trainer.isCurrentBugemonAlive()) {
            if (!trainer.isDefeated()) {
                LOG.info("{} fainted", trainer.getCurrentBugemonName());
                this.turnResult.addStep(new TurnStep.BugemonKoStep(trainer));
            } else {
                LOG.info("{} is defeated", trainer.getCurrentBugemonName());
                this.turnResult.addStep(new TurnStep.TrainerKoStep(trainer));
                this.isCompleted = true;
            }
        }
    }

    private void handleItem(Trainer trainer, Item item) {
        trainer.applyPassiveAction(new TurnAction.UseItemAction(item));
    }

    private void handleSwitch(Trainer trainer, Bugemon bugemon) {
        trainer.setCurrentBugemon(bugemon);
    }

    private void resolveAutoAttack(TurnAction playerAction, TurnAction opponentAction) {
        Attack playerAttack = ((TurnAction.AttackAction) playerAction).attack();
        Attack opponentAttack = ((TurnAction.AttackAction) opponentAction).attack();
        this.applyDualAttack(this.playerTrainer, this.opponentTrainer, playerAttack, opponentAttack);
    }

    private void resolveDualAttack(Attack playerAttack, Attack opponentAttack) {
        Trainer firstAttacker = CombatService.attackPriority(this.playerTrainer, this.opponentTrainer);
        Trainer secondAttacker = firstAttacker == this.playerTrainer ? this.opponentTrainer : this.playerTrainer;

        Attack firstAttack = firstAttacker == this.playerTrainer ? playerAttack : opponentAttack;
        Attack secondAttack = secondAttacker == this.playerTrainer ? playerAttack : opponentAttack;

        this.applyDualAttack(firstAttacker, secondAttacker, firstAttack, secondAttack);
    }

    private void resolveSoloAttack(Trainer attacker, Attack attack, Trainer defender) {
        this.applyAttack(attacker, defender, attack);
    }

    private void applyDualAttack(Trainer firstAttacker, Trainer secondAttacker, Attack firstAttack,
            Attack secondAttack) {
        this.applyAttack(firstAttacker, secondAttacker, firstAttack);

        // No second attack if Bugemon is KO from first attack
        if (secondAttacker.isCurrentBugemonAlive()) {
            this.applyAttack(secondAttacker, firstAttacker, secondAttack);
        }
    }

    private void applyAttack(Trainer attacker, Trainer defender, Attack attack) {
        int damage = CombatService.calculateDamage(attack, attacker.getCurrentBugemon(), defender.getCurrentBugemon());
        defender.takeDamage(damage);

        this.applyAttackEffects(attack.effects(), attacker, defender);

        Efficiency efficiency = attack.getEfficiencyAgainst(defender.getCurrentBugemon());
        LOG.debug("{} used {} on {} — {} dmg [{}]", attacker.getCurrentBugemonName(), attack.name(),
                defender.getCurrentBugemonName(), damage, efficiency);

        this.turnResult.addStep(new TurnStep.AttackStep(attacker, attack, efficiency));
    }

    private void applyAttackEffects(List<Effect> effects, Trainer attacker, Trainer defender) {
        effects.forEach(e -> {
            switch (e.target()) {
                case OPPONENT -> attacker.getCurrentBugemon().apply(e);
                case TEAM -> attacker.applyEffectToCurrentTeam(e);
                case THROWER -> attacker.getCurrentBugemon().apply(e);
                default -> throw new IllegalStateException("unknown effect target");
            }
        });
    }

    public Trainer getPlayerTrainer() {
        return this.playerTrainer;
    }

    public Trainer getOpponentTrainer() {
        return this.opponentTrainer;
    }

    private Trainer getOtherTrainer(Trainer trainer) {
        return trainer.equals(this.playerTrainer) ? this.opponentTrainer : this.playerTrainer;
    }

    /**
     * Synchronizes the opponent AI trainer with the current player state.
     *
     * In manual combat, the player side is a ManualTrainer and only the opponent can be AI.
     */
    private void syncOpponentAiState() {
        if (this.opponentTrainer instanceof AITrainer aiTrainer) {
            aiTrainer.setOpponentTrainer(this.playerTrainer);
            aiTrainer.setOpponentActiveBugemon(this.playerTrainer.getCurrentBugemon());
        }
    }

    @FunctionalInterface
    public interface EndOfCombatAction {
        void execute(CombatContext ctx);

        /**
         * No action (as in does nothing).
         */
        EndOfCombatAction NO_OP = ctx -> {
        };

        EndOfCombatAction RESTORE_HP = ctx -> {
            ctx.winner().restoreTeamHp();
            ctx.loser().restoreTeamHp();
        };
    }
}
