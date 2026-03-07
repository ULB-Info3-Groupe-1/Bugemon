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
 * The AutomaticCombat class represents a combat between two trainers where the
 * actions are automatically determined by the system.
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
    public AutomaticCombat(
        AutoTrainer allyTrainer,
        AutoTrainer adversaryTrainer
    ) {
        super(allyTrainer, adversaryTrainer);
    }

    // Methods

    /**
     * Simulates a turn of the combat.
     */
    public void turn() {
        applyDamage();
    }

    /**
     * Applies the damage of the attacks of both trainers to each other and returns
     * the winner of the turn if there is one.
     *
     * @return (AutoTrainer) the winner of the turn if there is one, null otherwise.
     */
    private void applyDamage() {
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
    }

    /**
     * Helper method to apply damage from one trainer to another and check if the
     * trainer is defeated.
     *
     * @param attacker    (AutoTrainer) the trainer who is attacking.
     * @param defender    (AutoTrainer) the trainer who is defending.
     * @param attackPower (int) the power of the attack being applied.
     * @return (AutoTrainer) the winner of the turn if defender is defeated, null
     *         otherwise.
     */
    private void applyDamageHelper(
        AutoTrainer attacker,
        AutoTrainer defender,
        int attackPower
    ) {
        defender.takeDamage(attackPower);

        if (defender.isDefeated()) {
            return;
        }
        if (!defender.isCurrentBugemonAlive()) {
            defender.selectRandomBugemon();
        }
    }
}
