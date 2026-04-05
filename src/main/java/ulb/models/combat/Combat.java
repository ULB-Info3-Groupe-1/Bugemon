package ulb.models.combat;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;
import ulb.models.trainer.TurnAction;
import ulb.services.CombatService;

/**
 * Orchestrates a turn-based combat between two {@link Trainer}s. Each call to {@link #turn()} asks both trainers for
 * their {@link TurnAction}, applies passive actions (switches, …), then resolves attacks in initiative order. The
 * combat ends when {@link #isFinished()} returns {@code true}; {@link #getWinner()} then identifies the survivor.
 *
 * To add a new passive action: add a branch in {@link #applyPassiveAction(Trainer, TurnAction)} — no other method needs
 * to change.
 */
public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);

    private final Trainer allyTrainer;
    private final Trainer adversaryTrainer;

    /** Zero-based, incremented after every non-forfeit turn. */
    private int turn = 0;

    private TurnResult lastTurnResult;

    public Combat(Trainer allyTrainer, Trainer adversaryTrainer) {
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
        LOG.info("Combat started — ally: {} vs adversary: {}", allyTrainer.getCurrentBugemonName(),
                adversaryTrainer.getCurrentBugemonName());
    }

    /**
     * Resolves one full round and returns a {@link TurnResult} describing every hit. Sequence: mark participation → get
     * actions → handle forfeit → apply passive actions → resolve attacks in initiative order → increment turn counter.
     */
    public TurnResult turn() {
        LOG.debug("Turn {} — ally: {} ({}hp) vs adversary: {} ({}hp)", this.turn,
                this.allyTrainer.getCurrentBugemonName(), this.allyTrainer.getCurrentBugemonHp(),
                this.adversaryTrainer.getCurrentBugemonName(), this.adversaryTrainer.getCurrentBugemonHp());

        this.allyTrainer.markCurrentBugemonParticipation();
        this.adversaryTrainer.markCurrentBugemonParticipation();

        TurnAction allyAction = null;
        TurnAction adversaryAction = null;

        if (this.allyTrainer.isCurrentBugemonAlive()) {
            allyAction = this.allyTrainer.getAction();
        }

        if (this.adversaryTrainer.isCurrentBugemonAlive()) {
            adversaryAction = this.adversaryTrainer.getAction();
        }

        if (this.checkForForfeit(allyAction, adversaryAction) || !this.allyTrainer.isCurrentBugemonAlive()) {
            LOG.info("Turn {} ended by forfeit or KO", this.turn);
            this.lastTurnResult = this.emptyResult();
            return this.lastTurnResult;
        }

        this.applyPassiveAction(this.allyTrainer, allyAction);
        this.applyPassiveAction(this.adversaryTrainer, adversaryAction);

        Optional<Attack> allyAttack = this.extractAttack(allyAction);
        Optional<Attack> adversaryAttack = this.extractAttack(adversaryAction);

        this.turn++;
        this.lastTurnResult = this.resolveAttacks(allyAttack, adversaryAttack);
        return this.lastTurnResult;
    }

    /** @return empty if combat is still ongoing */
    public Optional<Trainer> getWinner() {
        if (this.allyTrainer.isDefeated()) {
            LOG.info("Combat finished after {} turn(s) — adversary wins", this.turn);
            return Optional.of(this.adversaryTrainer);
        }

        if (this.adversaryTrainer.isDefeated()) {
            LOG.info("Combat finished after {} turn(s) — ally wins", this.turn);
            return Optional.of(this.allyTrainer);
        }

        return Optional.empty();
    }

    public boolean isFinished() {
        return this.allyTrainer.isDefeated() || this.adversaryTrainer.isDefeated();
    }

    public Trainer getAllyTrainer() {
        return this.allyTrainer;
    }

    public Trainer getAdversaryTrainer() {
        return this.adversaryTrainer;
    }

    /** @return {@code null} before the first turn */
    public TurnResult getLastTurnResult() {
        return this.lastTurnResult;
    }

    public int getTurn() {
        return this.turn;
    }

    private boolean checkForForfeit(TurnAction allyAction, TurnAction adversaryAction) {
        if (allyAction instanceof TurnAction.ForfeitAction) {
            this.allyTrainer.killTeam();
            return true;
        }
        if (adversaryAction instanceof TurnAction.ForfeitAction) {
            this.adversaryTrainer.killTeam();
            return true;
        }
        return false;
    }

    private void applyPassiveAction(Trainer trainer, TurnAction action) {
        LOG.debug("Passive action — {}: {}", trainer.getCurrentBugemonName(), action);
        trainer.applyPassiveAction(action);
    }

    private Optional<Attack> extractAttack(TurnAction action) {
        if (action instanceof TurnAction.AttackAction(Attack a)) {
            return Optional.of(a);
        }
        return Optional.empty();
    }

    /**
     * Handles all three cases: both attack (initiative order), only one attacks, neither attacks. The second hit is
     * skipped if the combat is already finished after the first.
     */
    private TurnResult resolveAttacks(Optional<Attack> allyAttack, Optional<Attack> adversaryAttack) {
        if (allyAttack.isPresent() && adversaryAttack.isPresent()) {
            Trainer first = CombatService.attackPriority(this.allyTrainer, this.adversaryTrainer);
            Trainer second = first == this.allyTrainer ? this.adversaryTrainer : this.allyTrainer;
            Attack firstAttack = first == this.allyTrainer ? allyAttack.get() : adversaryAttack.get();
            Attack secondAttack = second == this.allyTrainer ? allyAttack.get() : adversaryAttack.get();

            TurnResult.AttackResult firstResult = this.applyAttack(first, second, firstAttack);
            Optional<TurnResult.AttackResult> secondResult = this.isFinished() ? Optional.empty()
                    : Optional.of(this.applyAttack(second, first, secondAttack));

            boolean allyKO = this.isAllyKo(firstResult, secondResult);
            return new TurnResult(firstResult, secondResult, allyKO);
        }

        if (allyAttack.isPresent()) {
            TurnResult.AttackResult hit = this.applyAttack(this.allyTrainer, this.adversaryTrainer, allyAttack.get());
            return new TurnResult(hit, Optional.empty(), false);
        }

        if (adversaryAttack.isPresent()) {
            TurnResult.AttackResult hit = this.applyAttack(this.adversaryTrainer, this.allyTrainer,
                    adversaryAttack.get());
            boolean allyKO = !this.allyTrainer.isCurrentBugemonAlive() && !this.allyTrainer.isDefeated();
            return new TurnResult(hit, Optional.empty(), allyKO);
        }

        return this.emptyResult();
    }

    private boolean isAllyKo(TurnResult.AttackResult first, Optional<TurnResult.AttackResult> second) {
        boolean koByFirst = first.defender() == this.allyTrainer && !this.allyTrainer.isCurrentBugemonAlive();
        boolean koBySecond = second.isPresent() && second.get().defender() == this.allyTrainer
                && !this.allyTrainer.isCurrentBugemonAlive();
        return koByFirst || koBySecond;
    }

    /**
     * Applies damage, computes type efficiency, triggers KO reaction if the defender faints. Effects are NOT applied on
     * KO (the Bugemon is switched out before they could take effect).
     */
    private TurnResult.AttackResult applyAttack(Trainer attacker, Trainer defender, Attack attack) {
        int damage = CombatService.calculateDamage(attack, attacker.getCurrentBugemon(), defender.getCurrentBugemon());
        defender.takeDamage(damage);

        Efficiency efficiency = CombatService.compareBugemonType(attack.type(), defender.getCurrentBugemonType());

        LOG.debug("{} used {} on {} — {} dmg [{}]", attacker.getCurrentBugemonName(), attack.name(),
                defender.getCurrentBugemonName(), damage, efficiency);

        if (!defender.isCurrentBugemonAlive() && !defender.isDefeated()) {
            LOG.info("{} fainted", defender.getCurrentBugemonName());
            defender.reactToKo();
        }
        return new TurnResult.AttackResult(attacker, defender, Optional.of(attack), efficiency);
    }

    private TurnResult emptyResult() {
        return new TurnResult(
                new TurnResult.AttackResult(this.allyTrainer, this.adversaryTrainer, Optional.empty(), null),
                Optional.of(
                        new TurnResult.AttackResult(this.adversaryTrainer, this.allyTrainer, Optional.empty(), null)),
                true);
    }
}
