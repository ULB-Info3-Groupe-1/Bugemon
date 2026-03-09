/**
 * File name : ManualCombat.java
 * Description : Class representing a manual combat.
 *
 * @author Liefferinckx Romain
 * @date 02 March. 2026
 * @version 1.0
 */

package ulb.models.combat;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;

/**
 * Represents a player-driven combat between a {@link ManualTrainer} (the ally)
 * and an {@link AutoTrainer} (the adversary).
 *
 * <p>
 * Each turn is resolved by calling {@link #turn(ManualTrainer.TAction)} with
 * the action chosen by the player:
 * <ul>
 *   <li>{@link ManualTrainer.TAction#ATTACK} — the ally uses the attack
 *       previously set via {@link ManualTrainer#selectAttack}, and the
 *       adversary retaliates with a random attack.</li>
 *   <li>{@link ManualTrainer.TAction#SWITCH} — the ally swaps the active
 *       {@link ulb.models.bugemon.Bugemon} for the one set via
 *       {@link ManualTrainer#selectBugemon}; no damage is exchanged this
 *       turn.</li>
 *   <li>{@link ManualTrainer.TAction#FORFEIT} — the ally immediately concedes;
 *       the adversary is returned as the winner.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The turn counter is incremented at the end of every non-forfeit turn via
 * the inherited {@link Combat#incrementTurn()} method.
 * </p>
 *
 * @see Combat
 * @see ManualTrainer
 * @see AutoTrainer
 * @see ulb.models.combat.CombatHelper
 */
public class ManualCombat extends Combat {
    // Attributes
    private ManualTrainer allyTrainer;
    private AutoTrainer adversaryTrainer;

    // Constructor

    /**
     * Constructor for the ManualCombat class, initializing the two trainers of the
     * combat.
     *
     * @param allyTrainer      (ManualTrainer) the allied trainer participating in
     *                         the
     *                         combat.
     * @param adversaryTrainer (AutoTrainer) the adversary trainer participating in
     *                         the combat.
     */
    public ManualCombat(ManualTrainer allyTrainer, AutoTrainer adversaryTrainer) {
        super(allyTrainer, adversaryTrainer);
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
    }

    // Methods

    /**
     * Perform a turn of the combat, where the allied trainer selects an action and
     * the adversary trainer responds accordingly. The method returns the winning
     * trainer if the combat is over, or null if the combat is still ongoing.
     *
     * @return (Trainer) the winning trainer if the combat is over, or null if the
     *         combat is still ongoing.
     */
    public Trainer turn() {
        // TODO: Refactor this method
        this.allyTrainer.getCurrentBugemon().setParticipation(true);
        this.adversaryTrainer.getCurrentBugemon().setParticipation(true);
        switch (this.allyTrainer.getSelectedAction()) {
            case ATTACK:
                Attack allyAttack = this.allyTrainer.getSelectedAttack();
                if (allyAttack != null) {
                    applyDamage();
                }
                if (this.allyTrainer.isDefeated()) {
                    return this.adversaryTrainer;
                } else if (this.adversaryTrainer.isDefeated()) {
                    return this.allyTrainer;
                }
                break;
            case SWITCH:
                Bugemon selectedBugemon = this.allyTrainer.getSelectedBugemon();
                if (selectedBugemon != null) {
                    this.allyTrainer.setCurrentBugemon(selectedBugemon);
                }
                break;
            case FORFEIT:
                return this.adversaryTrainer;
            default:
                throw new IllegalArgumentException("Illegal action: "
                                                   + this.allyTrainer.getSelectedAction());
        }
        incrementTurn();
        return null;
    }

    /**
     * Resolves the damage exchange for an {@link ManualTrainer.TAction#ATTACK}
     * turn.
     *
     * <p>
     * The sequence of events is:
     * <ol>
     *   <li>The adversary selects a random attack for retaliation.</li>
     *   <li>The ally's selected attack power is applied to the adversary's
     *       active {@link ulb.models.bugemon.Bugemon}.</li>
     *   <li>If the adversary is fully defeated, the method returns early.</li>
     *   <li>If the adversary's active Bugemon fainted but the trainer is not
     *       yet defeated, the adversary switches to a random alive Bugemon
     *       and the method returns (the ally does not take damage this turn).</li>
     *   <li>Otherwise the adversary's random attack power is applied to the
     *       ally's active Bugemon.</li>
     *   <li>If the ally is fully defeated, the method returns early.</li>
     *   <li>If the ally's active Bugemon fainted, the ally must manually select
     *       a new Bugemon via {@link ManualTrainer#getSelectedBugemon()} (the
     *       switch itself is handled externally by the controller).</li>
     * </ol>
     * </p>
     */
    private void applyDamage() {
        Attack adversaryAttack = this.adversaryTrainer.getRandomAttack();
        int allyAttackPower = this.allyTrainer.getSelectedAttack().getPower();

        this.adversaryTrainer.takeDamage(allyAttackPower);

        if (this.adversaryTrainer.isDefeated()) {
            return;
        }

        if (!this.adversaryTrainer.isCurrentBugemonAlive()) {
            this.adversaryTrainer.selectRandomBugemon();
            return;
        }

        this.allyTrainer.takeDamage(adversaryAttack.getPower());

        if (this.allyTrainer.isDefeated()) {
            return;
        }

        if (!this.allyTrainer.isCurrentBugemonAlive()) {
            this.allyTrainer.getSelectedBugemon();
        }
    }
}
