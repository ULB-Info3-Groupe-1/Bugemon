package ulb.models.combat;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
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
    private final Trainer playerTrainer;
    private final Trainer opponentTrainer;

    private TurnResult turnResult;

    public Combat(Trainer playerTrainer, Trainer opponentTrainer) {
        this.playerTrainer = playerTrainer;
        this.opponentTrainer = opponentTrainer;
    }

    /**
     * Resolves one full round and returns a {@link TurnResult} describing every step. Actions are fetched exactly once
     * per trainer. Sequence: mark participation → get actions → handle forfeit → apply passive actions → resolve
     * attacks in initiative order → update trainer status.
     */
    public TurnResult turn() {
        this.turnResult = new TurnResult();

        this.markParticipation();

        TurnAction playerAction = this.playerTrainer.getAction();
        TurnAction opponentAction = this.opponentTrainer.getAction();

        if (!this.resolveForfeit(playerAction, opponentAction)) {
            this.resolveTurn(playerAction, opponentAction);
        }

        this.endTurn();

        return this.turnResult;
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

    private void endTurn() {
        this.updateTrainerStatus(this.playerTrainer);
        this.updateTrainerStatus(this.opponentTrainer);
    }

    private void resolveItem(TurnAction playerAction, TurnAction opponentAction) {
        if (playerAction instanceof TurnAction.UseItemAction useItemAction) {
            this.handleItem((ManualTrainer) this.playerTrainer, useItemAction.item());
            this.turnResult.addStep(new TurnStep.ItemStep(this.playerTrainer, useItemAction.item()));
        }
        if (opponentAction instanceof TurnAction.UseItemAction useItemAction) {
            this.handleItem((ManualTrainer) this.opponentTrainer, useItemAction.item());
            this.turnResult.addStep(new TurnStep.ItemStep(this.opponentTrainer, useItemAction.item()));
        }
    }

    private void resolveSwitch(TurnAction playerAction, TurnAction opponentAction) {
        if (playerAction instanceof TurnAction.SwitchAction switchAction) {
            this.handleSwitch(this.playerTrainer, switchAction.target());
            this.turnResult.addStep(new TurnStep.SwitchStep(this.playerTrainer, switchAction.target()));
        }
        if (opponentAction instanceof TurnAction.SwitchAction switchAction) {
            this.handleSwitch(this.opponentTrainer, switchAction.target());
            this.turnResult.addStep(new TurnStep.SwitchStep(this.opponentTrainer, switchAction.target()));
        }
    }

    private void resolveAttack(TurnAction playerAction, TurnAction opponentAction) {
        // Automatic combat does not consider initiative
        if (this.playerTrainer instanceof AutoTrainer) {
            this.resolveAutoAttack(playerAction, opponentAction);
            return;
        }

        if (playerAction instanceof TurnAction.AttackAction playerAttackAction) {
            if (opponentAction instanceof TurnAction.AttackAction opponentAttackAction) {
                this.resolveDualAttack(playerAttackAction.attack(), opponentAttackAction.attack());
                return;
            }
            // Only the player trainer attacked
            this.resolveSoloAttack(this.playerTrainer, playerAttackAction.attack(), this.opponentTrainer);
            return;
        }
        // Only the opponent trainer attacked
        if (opponentAction instanceof TurnAction.AttackAction opponentAttackAction) {
            this.resolveSoloAttack(this.opponentTrainer, opponentAttackAction.attack(), this.playerTrainer);
        }
    }

    private void handleForfeit(Trainer trainer) {
        trainer.killTeam(); // TODO: Better way to handle forfeit with new implementation ?
    }

    private void updateTrainerStatus(Trainer trainer) {
        if (!trainer.isCurrentBugemonAlive()) {
            if (!trainer.isDefeated()) {
                this.turnResult.addStep(new TurnStep.BugemonKoStep(trainer));
            } else {
                this.turnResult.addStep(new TurnStep.TrainerKoStep(trainer));
            }
        }
    }

    private void handleItem(ManualTrainer trainer, Item item) {
        trainer.useItem(item);
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

        Efficiency efficiency = CombatService.compareBugemonType(attack.type(), defender.getCurrentBugemonType());

        this.turnResult.addStep(new TurnStep.AttackStep(attacker, attack, efficiency));
    }

    public Trainer getPlayerTrainer() {
        return this.playerTrainer;
    }

    public Trainer getOpponentTrainer() {
        return this.opponentTrainer;
    }
}
