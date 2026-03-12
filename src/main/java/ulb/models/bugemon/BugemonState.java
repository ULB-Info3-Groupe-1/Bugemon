package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

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
class BugemonState {
    /** Current hit points of the bugemon. Serialised as {@code "pv"}. */
    @SerializedName("pv") int hp;

    /** Attack power of the bugemon. Serialised as {@code "attaque"}. */
    @SerializedName("attaque") int attack;

    /** Defensive rating of the bugemon. */
    int defense;

    /** Initiative value determining turn order in combat. */
    int initiative;

    /** Maximum hit points of the bugemon. (At the start of a battle) */
    int maxHp;

    /** Experience points (XP) of the bugemon. */
    int xp;

    /** Level of the bugemon. */
    int level;

    boolean participatedLastFight;

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
    public BugemonState(int hp, int attack, int defense, int initiative, int xp, int level) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.initiative = initiative;
        this.maxHp = hp;
        this.xp = xp;
        this.level = level;
        this.participatedLastFight = false;
    }

    /**
     * Copy-constructor. Creates an independent duplicate of the given
     * {@code State}.
     *
     * @param other the {@code State} instance to copy; must not be
     *              {@code null}.
     */
    public BugemonState(BugemonState other) {
        this.hp = other.hp;
        this.attack = other.attack;
        this.defense = other.defense;
        this.initiative = other.initiative;
        this.maxHp = other.maxHp;
        this.xp = other.xp;
        this.level = other.level;
        this.participatedLastFight = other.participatedLastFight;
    }
}