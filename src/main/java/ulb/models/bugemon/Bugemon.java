/**
 * File name : Bugemon.java
 * Description : Class representing a bugemon.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import ulb.common.BugemonDTO;

/**
 * This class represents a bugemon, which has an ID, name, type, and stats.
 */
public class Bugemon implements BugemonDTO {
    // Enums

    // Enum for the type of the bugemon
    public enum Type {
        FLORA, AQUA, PYRO, LITHO
    }

    // Attributes

    private String id;
    private String name;
    private Type type;
    private String sprite; // The link to the representation image of the bugemon
    private Stats stats;
    private AttackList attackList; // The list of attacks that the bugemon can have
    private boolean isStarter;

    // Constructor

    /**
     * Constructor for the Bugemon class, initializing all attributes.
     * 
     * @param id     (String) the unique identifier for the bugemon.
     * @param name   (String) the name of the bugemon.
     * @param type   (Type) the type of the bugemon.
     * @param sprite (String) the sprite link of the bugemon.
     * @param stats  (Stats) the stats of the bugemon, including HP, attack,
     *               defense,
     *               and initiative.
     */
    public Bugemon(String id, String name, Type type, String sprite, Stats stats, AttackList attackList,
            boolean isStarter) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sprite = sprite;
        this.stats = stats;
        this.attackList = attackList;
        this.isStarter = isStarter;
    }

    // Methods

    /**
     * Apply damage to the bugemon, reducing its HP by the specified amount.
     * 
     * @param damage (int) the amount of damage to apply to the bugemon, reducing
     *               its HP.
     */
    public void takeDamage(int damage) {
        int newHp = this.stats.getHp() - damage;
        this.stats.setHp(newHp);
    }

    /**
     * Check if the bugemon is alive, which is determined by whether its HP is
     * greater than 0.
     */
    public boolean isAlive() {
        return this.stats.getHp() > 0;
    }

    /**
     * Override the equals method to compare bugemons based on their unique ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Bugemon other = (Bugemon) obj;
        return id.equals(other.id);
    }

    /**
     * Override the hashCode method to generate a hash code based on the unique ID
     * of the bugemon.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Getters and Setters

    /**
     * Get the unique identifier of the bugemon.
     * 
     * @return (String) the unique identifier of the bugemon.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Set the unique identifier of the bugemon to a new value.
     * 
     * @param id (String) the new unique identifier to set for the bugemon.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of the bugemon.
     * 
     * @return (String) the name of the bugemon.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the bugemon to a new value.
     * 
     * @param name (String) the new name to set for the bugemon.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the bugemon.
     * 
     * @return (Type) the type of the bugemon.
     */
    public Type getType() {
        return type;
    }

    /**
     * Set the type of the bugemon to a new value.
     * 
     * @param type (Type) the new type to set for the bugemon.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get the image link of the bugemon.
     * 
     * @return (String) the sprite link of the bugemon.
     */
    @Override
    public String getSpriteURL() {
        return sprite;
    }

    /**
     * Set the sprite link of the bugemon to a new value.
     * 
     * @param sprite (String) the new sprite link to set for the bugemon.
     */
    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    /**
     * Get the stats of the bugemon.
     * 
     * @return (Stats) the stats of the bugemon, including HP, attack, defense,
     *         and initiative.
     */
    public Stats getStats() {
        return stats;
    }

    /**
     * Set the stats of the bugemon to new values.
     * 
     * @param stats (Stats) the new stats to set for the bugemon, including HP,
     *              attack, defense, and initiative.
     */
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * Get the list of attacks that the bugemon can have.
     * 
     * @return (AttackList) the list of attacks that the bugemon can have.
     */
    public AttackList getAttackList() {
        return attackList;
    }

    /**
     * Set the list of attacks that the bugemon can have to a new value.
     * 
     * @param attackList (AttackList) the new list of attacks to set for the
     *                   bugemon.
     */
    public void setAttackList(AttackList attackList) {
        this.attackList = attackList;
    }

    /**
     * Get whether the bugemon is a starter or not.
     * 
     * @return (boolean) true if the bugemon is a starter, false otherwise.
     */
    public boolean isStarter() {
        return isStarter;
    }

    /**
     * Set whether the bugemon is a starter or not to a new value.
     * 
     * @param isStarter (boolean) the new value to set for whether the bugemon is a
     *                  starter or not.
     */
    public void setStarter(boolean isStarter) {
        this.isStarter = isStarter;
    }
}
