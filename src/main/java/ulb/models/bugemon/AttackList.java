/**
 * File name : AttackList.java
 * Description : Class representing a list of attacks.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import java.util.List;

/**
 * This class represents a list of attacks that a bugemon can have.
 */
public class AttackList {
    // Attributes

    private List<Attack> attacks;

    // Constructor
    /**
     * Constructor for the AttackList class, initializing the list of attacks.
     * 
     * @param attacks (List<Attack>) the list of attacks that the bugemon can have.
     */
    public AttackList(List<Attack> attacks) {
        this.attacks = attacks;
    }

    // Getters and Setters

    /**
     * Get the list of attacks that the bugemon can have.
     * 
     * @return (List<Attack>) the list of attacks that the bugemon can have.
     */
    public List<Attack> getAttacks() {
        return attacks;
    }

    /**
     * Set the list of attacks that the bugemon can have to a new value.
     * 
     * @param attacks (List<Attack>) the new list of attacks to set for the bugemon.
     */
    public void setAttacks(List<Attack> attacks) {
        this.attacks = attacks;
    }
}
