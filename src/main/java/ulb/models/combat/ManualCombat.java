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
    public ManualCombat(
        ManualTrainer allyTrainer,
        AutoTrainer adversaryTrainer
    ) {
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
                throw new IllegalArgumentException("Illegal action: " + this.allyTrainer.getSelectedAction());
        }
        incrementTurn();
        return null;
    }

    /**
     * Apply the damage of the attack selected by the allied trainer to the
     * adversary trainer, and the damage of a random attack of the adversary trainer
     * to the allied trainer.
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
