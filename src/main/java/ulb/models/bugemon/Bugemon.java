/**
 * File name : Bugemon.java
 * Description : Class representing a bugemon.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ulb.common.BugemonDTO;

/**
 * This class represents a bugemon, which has an ID, name, type, and stats.
 */
public class Bugemon implements BugemonDTO, Cloneable {

    /**
     * This enum represents the type of a bugemon.
     */
    public enum BType {
        FLORA,
        AQUA,
        PYRO,
        LITHO,
    }

    private class State {

        @SerializedName("pv")
        private int hp;

        @SerializedName("attaque")
        private int attack;

        private int defense;
        private int initiative;

        public State(int hp, int attack, int defense, int initiative) {
            this.hp = hp;
            this.attack = attack;
            this.defense = defense;
            this.initiative = initiative;
        }

        public State(State other) {
            this.hp = other.hp;
            this.attack = other.attack;
            this.defense = other.defense;
            this.initiative = other.initiative;
        }
    }

    // Attributes

    private String id;

    @SerializedName("nom")
    private String name;

    private BType type;
    private String sprite;

    State initialState;
    State state;

    @SerializedName("starter")
    private boolean isStarter;

    @SerializedName("attaques")
    private List<Attack> attackList;

    /**
     * Private constructor used by the builder.
     */
    private Bugemon() {}

    /**
     * Builder for new instances
     *
     * Builder for new instancesc of Bugemon.
     */
    public static class Builder {

        private static final String DEFAULT_NAME = "default name";
        private static final BType DEFAULT_TYPE = BType.AQUA;
        private static final String DEFAULT_SPRITE = "/png/unknown.png";
        private static final int DEFAULT_HP = 100;
        private static final boolean DEFAULT_IS_STARTER = false;
        private static final int DEFAULT_ATTACK = 10;
        private static final int DEFAULT_DEFENSE = 10;
        private static final int DEFAULT_INITIATIVE = 10;

        private Optional<String> id = Optional.empty();

        private Optional<String> name = Optional.empty();
        private Optional<BType> type = Optional.empty();
        private Optional<String> sprite = Optional.empty();

        private Optional<Integer> hp = Optional.empty();
        private Optional<Integer> attack = Optional.empty();
        private Optional<Integer> defense = Optional.empty();
        private Optional<Integer> initiative = Optional.empty();

        private Optional<Boolean> isStarter = Optional.empty();
        private Optional<List<Attack>> attackList = Optional.empty();

        public Builder id(String id) {
            this.id = Optional.of(id);
            return this;
        }

        public Builder name(String name) {
            this.name = Optional.of(name);
            return this;
        }

        public Builder type(BType type) {
            this.type = Optional.of(type);
            return this;
        }

        public Builder sprite(String sprite) {
            this.sprite = Optional.of(sprite);
            return this;
        }

        public Builder hp(int hp) {
            this.hp = Optional.of(hp);
            return this;
        }

        public Builder attack(int attack) {
            this.attack = Optional.of(attack);
            return this;
        }

        public Builder defense(int defense) {
            this.defense = Optional.of(defense);
            return this;
        }

        public Builder initiative(int initiative) {
            this.initiative = Optional.of(initiative);
            return this;
        }

        public Builder addAttack(Attack attack) {
            this.attackList = Optional.of(attackList.orElseGet(ArrayList::new));

            this.attackList.get().add(attack);

            return this;
        }

        public Builder isStarter(boolean isStarter) {
            this.isStarter = Optional.of(isStarter);
            return this;
        }

        public Bugemon build() {
            Bugemon bugemon = new Bugemon();

            // NOTE: ID has no default value
            bugemon.id = this.id.orElseThrow(() ->
                new IllegalStateException("Bugemon id must be provided")
            );

            bugemon.name = this.name.orElse(DEFAULT_NAME);
            bugemon.type = this.type.orElse(DEFAULT_TYPE);
            bugemon.sprite = this.sprite.orElse(DEFAULT_SPRITE);

            bugemon.initialState = bugemon.new State(
                this.hp.orElse(DEFAULT_HP),
                this.attack.orElse(DEFAULT_ATTACK),
                this.defense.orElse(DEFAULT_DEFENSE),
                this.initiative.orElse(DEFAULT_INITIATIVE)
            );

            bugemon.state = bugemon.new State(bugemon.initialState);

            bugemon.attackList = this.attackList.orElseGet(ArrayList::new); // defaults to empty attacklist
            bugemon.isStarter = this.isStarter.orElse(DEFAULT_IS_STARTER);

            return bugemon;
        }
    }

    // Constructor

    /**
     * Make a copy of a Bugemon
     *
     * @param other A bugemon class
     */
    public Bugemon(Bugemon other) {
        this.id = other.id;
        this.name = other.name;
        this.type = other.type;
        this.sprite = other.sprite;

        this.state = new State(other.state);
        this.initialState = new State(other.initialState);

        this.isStarter = other.isStarter;
        this.attackList = other.attackList;
    }

    // Methods

    /**
     * Apply damage to the bugemon, reducing its HP by the specified amount.
     *
     * @param damage (int) the amount of damage to apply to the bugemon, reducing
     *               its HP.
     */
    public void takeDamage(int damage) {
        this.state.hp -= damage;
    }

    /**
     * Check if the bugemon is alive, which is determined by whether its HP is
     * greater than 0.
     */
    public boolean isAlive() {
        return this.state.hp > 0;
    }

    /**
     * Override the equals method to compare bugemons based on their unique ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bugemon other = (Bugemon) obj;
        return this.id.equals(other.id);
    }

    /**
     * Override the hashCode method to generate a hash code based on the unique ID
     * of the bugemon.
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    // Getters and Setters

    /**
     * Get the unique identifier of the bugemon.
     *
     * @return (String) the unique identifier of the bugemon.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get the name of the bugemon.
     *
     * @return (String) the name of the bugemon.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the type of the bugemon.
     *
     * @return (Type) the type of the bugemon.
     */
    public BType getType() {
        return this.type;
    }

    /**
     * Get the image link of the bugemon.
     *
     * @return (String) the sprite link of the bugemon.
     */
    public String getSpriteURL() {
        return this.sprite;
    }

    /**
     * Get the list of attacks that the bugemon can have.
     *
     * @return (AttackList) the list of attacks that the bugemon can have.
     */
    public List<Attack> getAttackList() {
        return this.attackList;
    }

    /**
     * Get whether the bugemon is a starter or not.
     *
     * @return (boolean) true if the bugemon is a starter, false otherwise.
     */
    public boolean isStarter() {
        return this.isStarter;
    }
}
