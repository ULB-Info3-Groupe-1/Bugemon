/**
 * File name : Attack.java
 * Description : Data class representing an attack of a bugemon.
 * 
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

/**
 * Class representing an attack that a bugemon can perform during battle.
 */
public class Attack {
    // Attributes

    private String id;
    private String name;
    private Type type;
    private String description;
    private int power;
    private Effect effect;

    // Constructor
    /**
     * Constructor for the Attack class, initializing all attributes.
     * 
     * @param id          (String) the unique identifier for the attack.
     * @param name        (String) the name of the attack.
     * @param type        (Type) the type of the attack.
     * @param description (String) a description of the attack's effects and
     *                    mechanics.
     * @param power       (int) the power of the attack, determining its damage
     *                    output.
     * @param effect      (Effect) the effect of the attack, which can apply
     *                    status changes or stat modifications to the target.
     */
    public Attack(String id, String name, Type type, String description, int power, Effect effect) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.power = power;
        this.effect = effect;
    }

    // Getters and Setters

    /**
     * Get the unique identifier of the attack.
     * 
     * @return (String) the unique identifier of the attack.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the unique identifier of the attack to a new value.
     * 
     * @param id (String) the new unique identifier to set for the attack.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of the attack.
     * 
     * @return (String) the name of the attack.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the attack to a new value.
     * 
     * @param name (String) the new name to set for the attack.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the attack.
     * 
     * @return (Type) the type of the attack.
     */
    public Type getType() {
        return type;
    }

    /**
     * Set the type of the attack to a new value.
     * 
     * @param type (Type) the new type to set for the attack.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get the description of the attack.
     * 
     * @return (String) the description of the attack.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the attack to a new value.
     * 
     * @param description (String) the new description to set for the attack.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the power of the attack.
     * 
     * @return (int) the power of the attack.
     */
    public int getPower() {
        return power;
    }

    /**
     * Set the power of the attack to a new value.
     * 
     * @param power (int) the new power to set for the attack.
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Get the effect of the attack.
     * 
     * @return (Effect) the effect of the attack.
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Set the effect of the attack to a new value.
     * 
     * @param effect (Effect) the new effect to set for the attack.
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
    }
}
