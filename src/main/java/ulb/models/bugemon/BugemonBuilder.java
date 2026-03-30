package ulb.models.bugemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.components.AttackComponent;
import ulb.models.bugemon.components.DefenseComponent;
import ulb.models.bugemon.components.HealthComponent;
import ulb.models.bugemon.components.InitiativeComponent;
import ulb.models.bugemon.components.LevelComponent;

/**
 * Fluent Bugemonbuilder for constructing {@link Bugemon} instances.
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
 *   <li>type        – {@link BugemonType#FLORA}</li>
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
 * Bugemon b = new Bugemon.BugemonBuilder()
 *     .id("001")
 *     .name("Florasect")
 *     .type(BType.FLORA)
 *     .hp(120)
 *     .build();
 * }</pre>
 */
public final class BugemonBuilder {
    /** Default display name applied when none is provided. */
    private static final String DEFAULT_NAME = "default name";

    /** Default elemental type applied when none is provided. */
    private static final BugemonType DEFAULT_TYPE = BugemonType.FLORA;

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
    private BugemonType type = DEFAULT_TYPE;

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
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder id(String id) {
        this.id = Optional.of(id);
        return this;
    }

    /**
     * Sets the display name for the bugemon under construction.
     *
     * @param name the non-null display name.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the elemental type for the bugemon under construction.
     *
     * @param type the non-null {@link BugemonType} to assign.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder type(BugemonType type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the sprite path or URL for the bugemon under construction.
     *
     * @param sprite the non-null sprite path or URL string.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder sprite(String sprite) {
        this.sprite = sprite;
        return this;
    }

    /**
     * Sets the initial hit-point value for the bugemon under construction.
     *
     * @param hp the hit-point value (typically a positive integer).
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder hp(int hp) {
        this.hp = hp;
        return this;
    }

    /**
     * Sets the attack stat for the bugemon under construction.
     *
     * @param attack the attack power value.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder attack(int attack) {
        this.attack = attack;
        return this;
    }

    /**
     * Sets the defense stat for the bugemon under construction.
     *
     * @param defense the defense rating value.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder defense(int defense) {
        this.defense = defense;
        return this;
    }

    /**
     * Sets the initiative stat for the bugemon under construction.
     *
     * @param initiative the initiative (turn-order priority) value.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder initiative(int initiative) {
        this.initiative = initiative;
        return this;
    }

    /**
     * Sets the experience points for the bugemon under construction.
     *
     * @param xp the experience points value
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder xp(int xp) {
        this.xp = xp;
        return this;
    }

    /**
     * Sets the level for the bugemon under construction.
     *
     * @param level the level value
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder level(int level) {
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
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder addAttack(Attack attack) {
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
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder attackList(List<Attack> attackList) {
        this.attackList = attackList;
        return this;
    }

    /**
     * Sets whether the bugemon under construction should be considered a
     * starter bugemon.
     *
     * @param isStarter {@code true} if the bugemon is a starter;
     *                  {@code false} otherwise.
     * @return this {@code BugemonBuilder} instance for method chaining.
     */
    public BugemonBuilder isStarter(boolean isStarter) {
        this.isStarter = isStarter;
        return this;
    }

    /**
     * Constructs and returns the configured {@link Bugemon} instance.
     * <p>
     * All fields that were not explicitly set will receive their default
     * values (see {@link BugemonBuilder} class-level documentation). The
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
        bugemon.id =
                this.id.orElseThrow(() -> new IllegalStateException("Bugemon id must be provided"));

        bugemon.name = this.name;
        bugemon.type = this.type;
        bugemon.sprite = this.sprite;

        bugemon.healthComponent = new HealthComponent(this.hp, this.hp);
        bugemon.attackComponent = new AttackComponent(this.attack);
        bugemon.defenseComponent = new DefenseComponent(this.defense);
        bugemon.initiativeComponent = new InitiativeComponent(this.initiative);
        bugemon.levelComponent = new LevelComponent(this.xp, this.level);

        bugemon.attackList = this.attackList;
        bugemon.isStarter = this.isStarter;

        return bugemon;
    }
}
