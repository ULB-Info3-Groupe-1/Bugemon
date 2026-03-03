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
     * @param action (Action) the action selected by the allied trainer for this
     *               turn, which can be an attack, a switch, or a forfeit.
     * @return (Trainer) the winning trainer if the combat is over, or null if the
     *         combat is still ongoing.
     */
    public Trainer turn(ManualTrainer.TAction action) {
        switch (action) {
            case ATTACK:
                Attack allyAttack = this.allyTrainer.getSelectedAttack();
                if (allyAttack != null) {
                    applyDamage();
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
                throw new IllegalArgumentException("Illegal action: " + action);
        }
        nextTurn();
        return null;
    }

    /**
     * Apply the damage of the attack selected by the allied trainer to the
     * adversary trainer, and the damage of a random attack of the adversary trainer
     * to the allied trainer.
     */
    private void applyDamage() {
        int adversaryAttack = this.adversaryTrainer.getRandomAttack();
        int allyAttackPower = this.allyTrainer.getSelectedAttack().getPower();
        this.adversaryTrainer.takeDamage(allyAttackPower);
        if (this.adversaryTrainer.isDefeated()) {
            return;
        }
        if (!this.adversaryTrainer.getCurrentBugemon().isAlive()) {
            this.adversaryTrainer.selectRandomBugemon();
        }
        this.allyTrainer.takeDamage(adversaryAttack);
        if (this.allyTrainer.isDefeated()) {
            return;
        }
        if (!this.allyTrainer.getCurrentBugemon().isAlive()) {
            this.allyTrainer.getSelectedBugemon();
        }
        return;
    }
}
