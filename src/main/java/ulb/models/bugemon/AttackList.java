/**
 * File name : AttackList.java
 * Description : Class representing a list of attacks.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This class represents a list of attacks that a bugemon can have.
 */
public class AttackList {

    // Attributes

    @SerializedName("attaques")
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

    /**
     * Constructor copy
     * @param other An other AttackList to copy
     */
    public AttackList(AttackList other) {
        this.attacks = other.attacks;
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

    public Attack get(int index) {
        return attacks.get(index);
    }

    public boolean contains(Attack attack) {
        return attacks.contains(attack);
    }

    /**
     * Get the number of attacks in the list.
     *
     * @return (int) the number of attacks in the list.
     */
    public int size() {
        return attacks.size();
    }
}
