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

    public void selectTAction() {
    }

    public Trainer turn(ManualTrainer.TAction action) {
        if (action == ManualTrainer.TAction.ATTACK) {
            Attack allyAttack = this.allyTrainer.getSelectedAttack();
            if (allyAttack != null) {
                applyDamage();
            }
        } else if (action == ManualTrainer.TAction.SWITCH) {
            Bugemon selectedBugemon = this.allyTrainer.getSelectedBugemon();
            if (selectedBugemon != null) {
                this.allyTrainer.setCurrentBugemon(selectedBugemon);
            }
        } else if (action == ManualTrainer.TAction.FORFEIT) {
            return adversaryTrainer;
        }
        ManualTrainer winner = (ManualTrainer) getWinner();
        if (winner != null) {
            return winner;
        }
        nextTurn();
        return null;
    }

    private void applyDamage() {
        AutoTrainer adversaryTrainer = (AutoTrainer) getAdversaryTrainer();
        ManualTrainer allyTrainer = (ManualTrainer) getAllyTrainer();
        int adversaryAttack = adversaryTrainer.getRandomAttack();
        int allyAttackPower = allyTrainer.getSelectedAttack().getPower();
        adversaryTrainer.takeDamage(allyAttackPower);
        if (adversaryTrainer.isDefeated()) {
            return;
        }
        if (!adversaryTrainer.getCurrentBugemon().isAlive()) {
            adversaryTrainer.selectRandomBugemon();
        }
        allyTrainer.takeDamage(adversaryAttack);
        if (allyTrainer.isDefeated()) {
            return;
        }
        if (!allyTrainer.getCurrentBugemon().isAlive()) {
            allyTrainer.getSelectedBugemon();
        }
        return;
    }
}
