/**
 * Nom du fichier : Bugemon.java
 * Description : Class representing a bugemon.
 * 
 * @author Liefferinckx Romain
 * @date 24 fÃ©vr. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

/**
 * This class represents a bugemon, which has an ID, name, type, and stats.
 */
public class Bugemon {
    // Attributes

    private String id;
    private String name;
    private String type;
    private String sprite; // The link to the representation image of the bugemon
    private Stats stats;
    private AttackList attackList; // The list of attacks that the bugemon can have
    private boolean isStarter;
    private boolean isAlive;

    // Constructor

    /**
     * Constructor for the Bugemon class, initializing all attributes.
     * 
     * @param id     (String) the unique identifier for the bugemon.
     * @param name   (String) the name of the bugemon.
     * @param type   (String) the type of the bugemon.
     * @param sprite (String) the sprite link of the bugemon.
     * @param stats  (Stats) the stats of the bugemon, including HP, attack,
     *               defense,
     *               and initiative.
     */
    public Bugemon(String id, String name, String type, String sprite, Stats stats, AttackList attackList,
            boolean isStarter) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sprite = sprite;
        this.stats = stats;
        this.attackList = attackList;
        this.isStarter = isStarter;
        this.isAlive = true;
    }

    // Methods

    /**
     * Apply damage to the bugemon, reducing its HP by the specified amount and
     * updating its alive status accordingly.
     * 
     * @param damage (int) the amount of damage to apply to the bugemon, reducing
     *               its HP.
     */
    public void takeDamage(int damage) {
        int newHp = this.stats.getHp() - damage;
        this.stats.setHp(newHp);
        setAlive();
    }

    /**
     * Set the alive status of the bugemon based on its current HP. If the HP is
     * less than or equal to 0, the bugemon is considered not alive; otherwise, it
     * is alive.
     */
    public void setAlive() {
        if (this.stats.getHp() <= 0) {
            this.isAlive = false;
        } else {
            this.isAlive = true;
        }
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
     * @return (String) the type of the bugemon.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the bugemon to a new value.
     * 
     * @param type (String) the new type to set for the bugemon.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the image link of the bugemon.
     * 
     * @return (String) the sprite link of the bugemon.
     */
    public String getSprite() {
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

    public boolean isAlive() {
        return isAlive;
    }
}
