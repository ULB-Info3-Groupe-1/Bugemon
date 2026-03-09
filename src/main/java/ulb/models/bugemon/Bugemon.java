/**
 * File name : Bugemon.java
 * Description : Class representing a bugemon.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import ulb.common.BugemonDTO;
import ulb.models.level_up.LevelUp;

/**
 * This class represents a bugemon, which has an ID, name, type, and stats.
 * <p>
 * A {@code Bugemon} is the central entity of the game. It holds identifying
 * information (ID, name, type, sprite), combat statistics (HP, attack, defense,
 * initiative), a list of available {@link Attack}s, and a flag indicating
 * whether the bugemon is a starter.
 * </p>
 * <p>
 * Instances must be created via the nested {@link Builder} class. The class
 * implements {@link Cloneable} to support deep-copying of bugemon instances,
 * and {@link BugemonDTO} to expose a common data-transfer interface.
 * </p>
 *
 * @see Builder
 * @see BType
 * @see Attack
 */
public class Bugemon implements BugemonDTO, Cloneable {
    /**
     * Represents the elemental type of a bugemon.
     * <p>
     * Each type may have strengths and weaknesses against other types during
     * combat.
     * </p>
     *
     * <ul>
     *   <li>{@link #FLORA}  – plant/nature-based type.</li>
     *   <li>{@link #AQUA}   – water-based type.</li>
     *   <li>{@link #PYRO}   – fire-based type.</li>
     *   <li>{@link #LITHO}  – rock/earth-based type.</li>
     * </ul>
     */
    public enum BType {
        /** Plant/nature-based elemental type. */
        FLORA,
        /** Water-based elemental type. */
        AQUA,
        /** Fire-based elemental type. */
        PYRO,
        /** Rock/earth-based elemental type. */
        LITHO,
    }

    /**
     * Encapsulates the mutable combat statistics of a {@link Bugemon}.
     * <p>
     * A {@code State} holds the four core stats used during battle:
     * hit points ({@code hp}), attack power ({@code attack}),
     * defensive rating ({@code defense}), and turn-order priority
     * ({@code initiative}).
     * </p>
     * <p>
     * Two constructors are provided: one to initialise from raw values, and a
     * copy-constructor to create an independent duplicate of an existing
     * {@code State}.
     * </p>
     */
    private class State {
        /** Current hit points of the bugemon. Serialised as {@code "pv"}. */
        @SerializedName("pv") private int hp;

        /** Attack power of the bugemon. Serialised as {@code "attaque"}. */
        @SerializedName("attaque") private int attack;

        /** Defensive rating of the bugemon. */
        private int defense;

        /** Initiative value determining turn order in combat. */
        private int initiative;

        /** Maximum hit points of the bugemon. (At the start of a battle) */
        private int maxHp;
        /** Experience points (XP) of the bugemon. */
        private int xp;

        /** Level of the bugemon. */
        private int level;

        /**
         * Constructs a new {@code State} with the given stat values.
         *
         * @param hp         the initial hit points.
         * @param attack     the attack power.
         * @param defense    the defense rating.
         * @param initiative the initiative (turn-order priority).
         * @param xp         the experience points (XP) of the bugemon.
         * @param level      the level of the bugemon.
         */
        public State(int hp, int attack, int defense, int initiative, int xp, int level) {
            this.hp = hp;
            this.attack = attack;
            this.defense = defense;
            this.initiative = initiative;
            this.maxHp = hp;
            this.xp = xp;
            this.level = level; // Default level
        }

        /**
         * Copy-constructor. Creates an independent duplicate of the given
         * {@code State}.
         *
         * @param other the {@code State} instance to copy; must not be
         *              {@code null}.
         */
        public State(State other) {
            this.hp = other.hp;
            this.attack = other.attack;
            this.defense = other.defense;
            this.initiative = other.initiative;
            this.maxHp = other.maxHp;
            this.xp = other.xp;
            this.level = other.level;

        }
    }

    /** Unique identifier of this bugemon. */
    private String id;

    /** Display name of this bugemon. Serialised as {@code "nom"}. */
    @SerializedName("nom") private String name;

    /** Elemental type of this bugemon. */
    private BType type;

    /** Path or URL to the sprite image of this bugemon. */
    private String sprite;

    /**
     * The baseline stats of this bugemon, set once at construction and used to
     * reset the bugemon to its original condition.
     */
    State initialState;

    /**
     * The current (mutable) combat stats of this bugemon. These values change
     * during battles (e.g. when damage is taken).
     */
    State state;

    /**
     * Whether this bugemon is available as a starter choice. Serialised as
     * {@code "starter"}.
     */
    @SerializedName("starter") private boolean isStarter;

    /**
     * The list of attacks available to this bugemon. Serialised as
     * {@code "attaques"}.
     */
    @SerializedName("attaques") private List<Attack> attackList;

    /**
     * Private no-arg constructor used exclusively by the {@link Builder}.
     * <p>
     * Direct instantiation is not supported; use {@link Builder} instead.
     * </p>
     */
    private Bugemon() {}

    /**
     * Fluent builder for constructing {@link Bugemon} instances.
     * <p>
     * Every property except {@code id} has a sensible default value so that
     * callers only need to supply the fields that differ from the defaults.
     * Calling {@link #build()} without setting an {@code id} will throw an
     * {@link IllegalStateException}.
     * </p>
     *
     * <p><b>Default values:</b></p>
     * <ul>
     *   <li>name        – {@value #DEFAULT_NAME}</li>
     *   <li>type        – {@link BType#AQUA}</li>
     *   <li>sprite      – {@value #DEFAULT_SPRITE}</li>
     *   <li>hp          – {@value #DEFAULT_HP}</li>
     *   <li>attack      – {@value #DEFAULT_ATTACK}</li>
     *   <li>defense     – {@value #DEFAULT_DEFENSE}</li>
     *   <li>initiative  – {@value #DEFAULT_INITIATIVE}</li>
     *   <li>xp          – {@value #DEFAULT_XP}</li>
     *   <li>isStarter   – {@value #DEFAULT_IS_STARTER}</li>
     *   <li>attackList  – empty list</li>
     * </ul>
     *
     * <p><b>Typical usage:</b></p>
     * <pre>{@code
     * Bugemon b = new Bugemon.Builder()
     *     .id("001")
     *     .name("Florasect")
     *     .type(BType.FLORA)
     *     .hp(120)
     *     .build();
     * }</pre>
     */
    public static class Builder {
        /** Default display name applied when none is provided. */
        private static final String DEFAULT_NAME = "default name";

        /** Default elemental type applied when none is provided. */
        private static final BType DEFAULT_TYPE = BType.FLORA;

        /** Default sprite path applied when none is provided. */
        private static final String DEFAULT_SPRITE = "/png/unknown.png";

        /** Default hit-point value applied when none is provided. */
        private static final int DEFAULT_HP = 100;

        /** Default starter flag applied when none is provided. */
        private static final boolean DEFAULT_IS_STARTER = false;

        /** Default attack stat applied when none is provided. */
        private static final int DEFAULT_ATTACK = 10;

        /** Default defense stat applied when none is provided. */
        private static final int DEFAULT_DEFENSE = 10;

        /** Default initiative stat applied when none is provided. */
        private static final int DEFAULT_INITIATIVE = 10;

        private static final int DEFAULT_XP = 0;

        private static final int DEFAULT_LEVEL = 1;

        /** The unique identifier to assign to the bugemon. */
        private Optional<String> id = Optional.empty();

        /** The display name to assign to the bugemon. */
        private String name = DEFAULT_NAME;

        /** The elemental type to assign to the bugemon. */
        private BType type = DEFAULT_TYPE;

        /** The sprite path/URL to assign to the bugemon. */
        private String sprite = DEFAULT_SPRITE;

        /** The hit-point value for the bugemon's initial state. */
        private int hp = DEFAULT_HP;

        /** The attack stat for the bugemon's initial state. */
        private int attack = DEFAULT_ATTACK;

        /** The defense stat for the bugemon's initial state. */
        private int defense = DEFAULT_DEFENSE;

        /** The initiative stat for the bugemon's initial state. */
        private int initiative = DEFAULT_INITIATIVE;

        /** The experience points for the bugemon's initial state. */
        private int xp = DEFAULT_XP;

        /** The level for the bugemon's initial state. */
        private int level = DEFAULT_LEVEL;

        /** Whether the bugemon should be flagged as a starter. */
        private boolean isStarter = DEFAULT_IS_STARTER;

        /** The list of attacks to assign to the bugemon. */
        private List<Attack> attackList = new ArrayList<>();

        /**
         * Sets the unique identifier for the bugemon under construction.
         * <p>
         * This field is <b>mandatory</b>; {@link #build()} will throw an
         * {@link IllegalStateException} if it is not supplied.
         * </p>
         *
         * @param id the non-null unique identifier string.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder id(String id) {
            this.id = Optional.of(id);
            return this;
        }

        /**
         * Sets the display name for the bugemon under construction.
         *
         * @param name the non-null display name.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the elemental type for the bugemon under construction.
         *
         * @param type the non-null {@link BType} to assign.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder type(BType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the sprite path or URL for the bugemon under construction.
         *
         * @param sprite the non-null sprite path or URL string.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder sprite(String sprite) {
            this.sprite = sprite;
            return this;
        }

        /**
         * Sets the initial hit-point value for the bugemon under construction.
         *
         * @param hp the hit-point value (typically a positive integer).
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder hp(int hp) {
            this.hp = hp;
            return this;
        }

        /**
         * Sets the attack stat for the bugemon under construction.
         *
         * @param attack the attack power value.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder attack(int attack) {
            this.attack = attack;
            return this;
        }

        /**
         * Sets the defense stat for the bugemon under construction.
         *
         * @param defense the defense rating value.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        /**
         * Sets the initiative stat for the bugemon under construction.
         *
         * @param initiative the initiative (turn-order priority) value.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder initiative(int initiative) {
            this.initiative = initiative;
            return this;
        }
        /**
         * Sets the experience points for the bugemon under construction.
         * @param xp
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder xp(int xp) {
            this.xp = xp;
            return this;
        }

        /**
         * Sets the level for the bugemon under construction.
         * @param level
         * @return
         */
        public Builder level(int level) {
            this.level = level;
            return this;
        }

        /**
         * Appends an {@link Attack} to the bugemon's attack list.
         * <p>
         * May be called multiple times to add several attacks. If no attack
         * list has been initialised yet, a new {@link ArrayList} is created
         * automatically.
         * </p>
         *
         * @param attack the non-null {@link Attack} to add.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder addAttack(Attack attack) {
            this.attackList.add(attack);
            return this;
        }

        /**
         * Sets the complete list of attacks for the bugemon under construction.
         * <p>
         * Replaces any attacks previously added via {@link #addAttack(Attack)}.
         * If individual attacks need to be appended incrementally, use
         * {@link #addAttack(Attack)} instead.
         * </p>
         *
         * @param attackList the non-null {@link List} of {@link Attack} instances
         *                   to assign to the bugemon.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder attackList(List<Attack> attackList) {
            this.attackList = attackList;
            return this;
        }

        /**
         * Sets whether the bugemon under construction should be considered a
         * starter bugemon.
         *
         * @param isStarter {@code true} if the bugemon is a starter;
         *                  {@code false} otherwise.
         * @return this {@code Builder} instance for method chaining.
         */
        public Builder isStarter(boolean isStarter) {
            this.isStarter = isStarter;
            return this;
        }

        /**
         * Constructs and returns the configured {@link Bugemon} instance.
         * <p>
         * All fields that were not explicitly set will receive their default
         * values (see {@link Builder} class-level documentation). The
         * bugemon's {@code state} is initialised as an independent copy of
         * {@code initialState}, so that the initial stats can always be
         * recovered.
         * </p>
         *
         * @return a fully initialised {@link Bugemon}.
         * @throws IllegalStateException if no {@code id} was provided via
         *                               {@link #id(String)}.
         */
        public Bugemon build() {
            Bugemon bugemon = new Bugemon();

            // NOTE: ID has no default value
            bugemon.id = this.id.orElseThrow(
                    () -> new IllegalStateException("Bugemon id must be provided"));

            bugemon.name = this.name;
            bugemon.type = this.type;
            bugemon.sprite = this.sprite;

            bugemon.initialState = bugemon.new State(
                this.hp,
                this.attack,
                this.defense,
                this.initiative,
                this.xp,
                this.level
            );

            bugemon.state = bugemon.new State(bugemon.initialState);

            bugemon.attackList = this.attackList;
            bugemon.isStarter = this.isStarter;

            return bugemon;
        }
    }

    /**
     * Creates and returns a deep copy of this Bugemon instance.
     * <p>
     * The cloned Bugemon will have independent copies of both the current
     * {@code state} and the {@code initialState}, ensuring that modifications
     * to the clone's state do not affect the original, and vice versa.
     * All other fields are shallow-copied via {@link Object#clone()}.
     * </p>
     *
     * @return a new {@code Bugemon} instance that is a deep copy of this object.
     * @throws CloneNotSupportedException if the object's class does not support
     *                                    the {@link Cloneable} interface.
     */
    @Override
    public Bugemon clone() throws CloneNotSupportedException {
        Bugemon cloned = (Bugemon)super.clone();
        cloned.state = new State(this.state);
        cloned.initialState = new State(this.initialState);

        return cloned;
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
     *
     * @return {@code true} if the bugemon's current HP is greater than 0;
     *         {@code false} otherwise.
     */
    public boolean isAlive() {
        return this.state.hp > 0;
    }

    /**
     * Override the equals method to compare bugemons based on their unique ID.
     *
     * @param obj the object to compare with this bugemon.
     * @return {@code true} if {@code obj} is a {@code Bugemon} with the same
     *         ID as this instance; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Bugemon other = (Bugemon)obj;
        return this.id.equals(other.id);
    }

    /**
     * Override the hashCode method to generate a hash code based on the unique ID
     * of the bugemon.
     *
     * @return an {@code int} hash code derived from the bugemon's unique ID.
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
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Get the name of the bugemon.
     *
     * @return (String) the name of the bugemon.
     */
    @Override
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
     * Add/Substract a given value to a given stat.
     *
     * @param stat  (EffectStat) The stat id to edit.
     * @param value (int) The value to add to the stat.
     */
    public void editStat(EffectStat stat, int value) throws KeyException {
        switch (stat) {
            case EffectStat.HP:
                this.state.hp = this.state.hp + value;
                break;
            case EffectStat.ATTACK:
                this.state.attack = this.state.attack + value;
                break;
            case EffectStat.DEFENSE:
                this.state.defense = this.state.defense + value;
                break;
            case EffectStat.INITIATIVE:
                this.state.initiative = this.state.initiative + value;
                break;
            default:
                throw new KeyException("Invalid stat key when trying to edit stat value");
        }
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
     * Get the current hit points of the bugemon.
     *
     * @return (int) the current hit points of the bugemon.
     */
    public int getHp() {
        return this.state.hp;
    }

    /**
     * Get the maximum hit points of the bugemon (at the start of a battle).
     * @return (int) the maximum hit points of the bugemon.
     */
    public int getMaxHp() {
        return this.state.maxHp;
    }

    /**
     * Get the attack power of the bugemon.
     *
     * @return (int) the current attack power of the bugemon.
     */
    public int getAttack() {
        return this.state.attack;
    }

    /**
     * Get the defense rating of the bugemon.
     *
     * @return (int) the current defense rating of the bugemon.
     */
    public int getDefense() {
        return this.state.defense;
    }

    /**
     * Get the initiative value of the bugemon, which determines turn order in
     * combat.
     *
     * @return (int) the initiative value of the bugemon.
     */
    public int getInitiative() {
        return this.state.initiative;
    }

    /**
     * Get whether the bugemon is a starter or not.
     *
     * @return (boolean) true if the bugemon is a starter, false otherwise.
     */
    public boolean isStarter() {
        return this.isStarter;
    }

    /**
     * Reset the bugemon's current state to its initial state, restoring its original stats.
     */
    public void reset() {
        this.state = new State(this.initialState);
    }

    @Override
    public int getLevel() {
        return this.state.level;
    }

    /**
     * Restore the bugemon's HP to its initial value, without affecting other stats.
     */
    public void restoreHp() {
        this.state.hp = this.initialState.hp;
    }

    /**
     * Get the experience points (XP) of the bugemon.
     *
     * @return (int) the current experience points of the bugemon.
     */
    public int getXp() {
        return this.state.xp;
    }

    /**
     * Add experience points (XP) to the bugemon and check if it should level up.
     *
     * @param xp the amount of XP to add to the bugemon's current XP total.
     */
    public Optional<LevelUp> addXp(int xp) {
        this.state.xp += xp;
        
        switch (this.state.level) {
            case 1 -> {
                if (this.state.xp >= 50){
                    return Optional.of(this.levelUp());
                }
            }
            case 2 -> {
                if (this.state.xp >= 150){
                    return Optional.of(this.levelUp());
                }
            }
            case 3 -> {
                if (this.state.xp >= 250){
                    return Optional.of(this.levelUp());
                }
            }
            case 4 -> {
                if (this.state.xp >= 350){
                    return Optional.of(this.levelUp());
                }
            }
            default -> {
                int required_xp = 50 + 50 * (this.state.level-1);
                if (this.state.xp >= required_xp){
                    return Optional.of(this.levelUp());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Level up the bugemon, increasing its level by 1.
     */
    public LevelUp levelUp() {
        this.state.xp = 0;
        this.state.level += 1;
        this.restoreHp();
        return new LevelUp(this);
    }
}
