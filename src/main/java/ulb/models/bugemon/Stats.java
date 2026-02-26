/**
 * File name : Stats.java
 * Description : Data class representing the stats of a bugemon.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

/**
 * This class represents the stats of a bugemon, including HP, attack, defense,
 * and initiative.
 */

public class Stats {
    // Attributes

    private int hp;
    private int attack;
    private int defense;
    private int initiative;

    // Constructor
    /**
     * Constructor for the Stats class, initializing all attributes.
     * 
     * @param hp         (int) the hit points of the bugemon, representing its
     *                   health.
     * @param attack     (int) the attack stat of the bugemon, determining its
     *                   damage output.
     * @param defense    (int) the defense stat of the bugemon, reducing incoming
     *                   damage.
     * @param initiative (int) the initiative stat of the bugemon, determining its
     *                   turn order
     */
    public Stats(int hp, int attack, int defense, int initiative) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.initiative = initiative;
    }

    // Getters and Setters

    /**
     * Get the current HP of the bugemon.
     * 
     * @return (int) the current HP of the bugemon.
     */
    public int getHp() {
        return hp;
    }

    /**
     * Set the HP of the bugemon to a new value.
     * 
     * @param hp (int) the new HP value to set for the bugemon.
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * Get the attack stat of the bugemon.
     * 
     * @return (int) the attack stat of the bugemon.
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Set the attack stat of the bugemon to a new value.
     * 
     * @param attack (int) the new attack value to set for the bugemon.
     */
    public void setAttack(int attack) {
        this.attack = attack;
    }

    /**
     * Get the defense stat of the bugemon.
     * 
     * @return (int) the defense stat of the bugemon.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Set the defense stat of the bugemon to a new value.
     * 
     * @param defense (int) the new defense value to set for the bugemon.
     */
    public void setDefense(int defense) {
        this.defense = defense;
    }

    /**
     * Get the initiative stat of the bugemon.
     * in battle.
     * 
     * @return (int) the initiative stat of the bugemon.
     */
    public int getInitiative() {
        return initiative;
    }

    /**
     * Set the initiative stat of the bugemon to a new value.
     * 
     * @param initiative (int) the new initiative value to set for the bugemon.
     */
    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }
}
