/**
 * File name : AutomaticCombat.java
 * Description : Class representing an automatic combat between two trainers.
 * 
 * @author Liefferinckx Romain
 * @date 28 feb. 2026
 * @version 1.0
 */

package ulb.models.combat;

import ulb.models.bugemon.AttackList;
import ulb.models.trainer.Trainer;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

/**
 * The AutomaticCombat class represents a combat between two trainers where the
 * actions are automatically determined by the system.
 */
public class AutomaticCombat extends Combat {

    // Attributes

    // Constructor

    /**
     * Constructor for the AutomaticCombat class, initializing the two trainers and
     * the turn of the combat.
     */
    public AutomaticCombat(Trainer trainer1, Trainer trainer2) {
        super(trainer1, trainer2);
    }

    // Methods

    /**
     * Choose a random attack from the list of attacks of the current bugemon of the
     * attacker.
     * 
     * @param attacks (AttackList) the list of attacks of the current bugemon of the
     *                attacker.
     * @return (Attack) a random attack from the list of attacks of the current
     *         bugemon of the attacker.
     */
    private Attack getRandomAttack(AttackList attacks) {
        Random rand = new Random();
        int attackIndex = rand.nextInt(attacks.getAttacks().size());
        return attacks.get(attackIndex);
    }

    /**
     * Apply the damage of the attack of the attacker to the defender, reducing the
     * HP of the current bugemon of the defender.
     * 
     * @param defender (Trainer) the trainer who is defending against the attack of
     *                 the attacker.
     * @param attacker (Trainer) the trainer who is attacking the defender.
     */
    private void applyDamage(Trainer defender, Trainer attacker) {
        int attackPower = attacker.getCurrentBugemon().getStats().getAttack();
        defender.getCurrentBugemon().takeDamage(attackPower);
        if (defender.getCurrentBugemon().getStats().getHp() <= 0) {
            defender.setCurrentBugemon(selectRandomBugemon(defender));
        }
    }

    /**
     * Choose a random bugemon from the team of the trainer.
     * 
     * @param trainer (Trainer) the trainer from which to choose a random bugemon.
     * @return (Bugemon) a random bugemon from the team of the trainer.
     */
    private Bugemon selectRandomBugemon(Trainer trainer) {
        if (trainer.isDefeated()) {
            return null;
        }

        List<Bugemon> aliveBugemons = new ArrayList<>();

        for (Bugemon b : trainer.getTeam().getTeam()) {
            if (b.isAlive()) {
                aliveBugemons.add(b);
            }
        }
        Random rand = new Random();
        int randomBugemonIndex = rand.nextInt(aliveBugemons.size());
        return aliveBugemons.get(randomBugemonIndex);
    }
}
