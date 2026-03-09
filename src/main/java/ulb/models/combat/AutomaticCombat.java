/**
 * File name : AutomaticCombat.java
 * Description : Class representing an automatic combat between two trainers.
 *
 * @author Liefferinckx Romain
 * @date 28 feb. 2026
 * @version 1.0
 */

package ulb.models.combat;

import ulb.models.bugemon.Attack;
import ulb.models.trainer.AutoTrainer;

/**
 * Represents a fully automated combat between two {@link AutoTrainer}s, where
 * both sides choose their actions randomly every turn.
 *
 * <p>
 * Each call to {@link #turn()} resolves a complete round of combat: both
 * trainers select a random attack, the damage is applied simultaneously, and
 * any trainer whose active {@link ulb.models.bugemon.Bugemon} faints
 * automatically switches to a randomly chosen alive replacement.
 * </p>
 *
 * <p>
 * The combat ends when at least one trainer has no more living Bugemons, as
 * detected by the inherited {@link Combat#isFinished()} method.
 * {@link Combat#getWinner()} then identifies the surviving trainer.
 * </p>
 *
 * @see Combat
 * @see AutoTrainer
 * @see ulb.models.combat.CombatHelper
 */
public class AutomaticCombat extends Combat {
    // Constructor

    /**
     * Constructor for the AutomaticCombat class, initializing the two trainers and
     * the turn of the combat.
     *
     * @param allyTrainer      (AutoTrainer) the allied trainer participating in the
     *                         combat.
     * @param adversaryTrainer (AutoTrainer) the adversary trainer participating in
     *                         the combat.
     */
    public AutomaticCombat(AutoTrainer allyTrainer, AutoTrainer adversaryTrainer) {
        super(allyTrainer, adversaryTrainer);
    }

    // Methods

    /**
     * Simulates one full round of combat.
     *
     * <p>
     * During a turn, both trainers independently select a random attack and
     * apply their damage to the opposing trainer's active
     * {@link ulb.models.bugemon.Bugemon}. If a Bugemon faints as a result,
     * the owning trainer automatically switches to a random alive replacement
     * (unless the trainer is fully defeated).
     * </p>
     *
     * @see #applyDamage()
     * @see Combat#isFinished()
     */
    public void turn() {
        this.allyTrainer.getCurrentBugemon().setParticipation(true);
        this.adversaryTrainer.getCurrentBugemon().setParticipation(true);
        applyDamage();
    }

    /**
     * Resolves the damage exchange for a single turn: each trainer picks a
     * random attack and deals its damage to the other trainer's active
     * {@link ulb.models.bugemon.Bugemon}.
     *
     * <p>
     * Both attacks are selected before either is applied, so the order of
     * resolution is: ally attacks adversary, then adversary attacks ally.
     * After each hit, {@link #applyDamageHelper} checks whether a switch is
     * necessary.
     * </p>
     */
    private Attack applyDamage() {
        AutoTrainer allyTrainer = (AutoTrainer) getAllyTrainer();
        AutoTrainer adversaryTrainer = (AutoTrainer) getAdversaryTrainer();
        Attack allyAttack = allyTrainer.getRandomAttack();
        Attack adversaryAttack = adversaryTrainer.getRandomAttack();
        applyDamageHelper(allyTrainer, adversaryTrainer, allyAttack.getPower());
        applyDamageHelper(
            adversaryTrainer,
            allyTrainer,
            adversaryAttack.getPower()
        );
        return allyAttack;
    }

    /**
     * Applies {@code attackPower} damage to the defender's active
     * {@link ulb.models.bugemon.Bugemon} and, if that Bugemon faints, triggers
     * an automatic switch to a random alive replacement.
     *
     * <p>
     * If the defender is fully defeated after taking damage (all Bugemons have
     * fainted), no switch is attempted and the method returns immediately.
     * </p>
     *
     * @param attacker    the {@link AutoTrainer} dealing the damage.
     * @param defender    the {@link AutoTrainer} receiving the damage.
     * @param attackPower the raw damage value to subtract from the defender's
     *                    active Bugemon's HP.
     * @see AutoTrainer#selectRandomBugemon()
     */
    private void applyDamageHelper(AutoTrainer attacker, AutoTrainer defender, int attackPower) {
        defender.takeDamage(attackPower);

        if (defender.isDefeated()) {
            return;
        }
        if (!defender.isCurrentBugemonAlive()) {
            defender.selectRandomBugemon();
        }
    }
}
