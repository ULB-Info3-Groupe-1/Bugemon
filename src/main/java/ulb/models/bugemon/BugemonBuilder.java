package ulb.models.bugemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.Configuration;
import ulb.models.bugemon.components.AttackComponent;
import ulb.models.bugemon.components.DefenseComponent;
import ulb.models.bugemon.components.HealthComponent;
import ulb.models.bugemon.components.InitiativeComponent;
import ulb.models.bugemon.components.LevelComponent;

/**
 * Fluent builder for {@link Bugemon}. All fields except {@code name} have defaults.
 *
 * Usage: {@code new BugemonBuilder().name("Florasect").hp(120).build()}
 */
@SuppressWarnings("checkstyle:HiddenField")
public final class BugemonBuilder {
    /** Default elemental type applied when none is provided. */
    private static final BugemonType DEFAULT_TYPE = BugemonType.FLORA;
    private static final String DEFAULT_SPRITE = Configuration.Paths.DEFAULT_SPRITE;
    private static final int DEFAULT_HP = 100;
    private static final boolean DEFAULT_IS_STARTER = false;
    private static final int DEFAULT_ATTACK = 10;
    private static final int DEFAULT_DEFENSE = 10;
    private static final int DEFAULT_INITIATIVE = 10;
    private static final int DEFAULT_XP = 0;
    private static final int DEFAULT_LEVEL = 1;

    /** The unique name to assign to the bugemon. */
    private Optional<String> name = Optional.empty();

    private BugemonType type = DEFAULT_TYPE;
    private String sprite = DEFAULT_SPRITE;
    private int hp = DEFAULT_HP;
    private int attack = DEFAULT_ATTACK;
    private int defense = DEFAULT_DEFENSE;
    private int initiative = DEFAULT_INITIATIVE;
    private int xp = DEFAULT_XP;
    private int level = DEFAULT_LEVEL;
    private boolean isStarter = DEFAULT_IS_STARTER;
    private List<Attack> attackList = new ArrayList<>();

    public BugemonBuilder name(String name) {
        this.name = Optional.of(name);
        return this;
    }

    public BugemonBuilder type(BugemonType type) {
        this.type = type;
        return this;
    }

    public BugemonBuilder sprite(String sprite) {
        this.sprite = sprite;
        return this;
    }

    public BugemonBuilder hp(int hp) {
        this.hp = hp;
        return this;
    }

    public BugemonBuilder attack(int attack) {
        this.attack = attack;
        return this;
    }

    public BugemonBuilder defense(int defense) {
        this.defense = defense;
        return this;
    }

    public BugemonBuilder initiative(int initiative) {
        this.initiative = initiative;
        return this;
    }

    public BugemonBuilder xp(int xp) {
        this.xp = xp;
        return this;
    }

    public BugemonBuilder level(int level) {
        this.level = level;
        return this;
    }

    /** Appends one attack; use {@link #attackList} to replace the whole list at once. */
    public BugemonBuilder addAttack(Attack attack) {
        this.attackList.add(attack);
        return this;
    }

    /** Replaces any attacks previously added via {@link #addAttack}. */
    public BugemonBuilder attackList(List<Attack> attackList) {
        this.attackList = attackList;
        return this;
    }

    public BugemonBuilder isStarter(boolean isStarter) {
        this.isStarter = isStarter;
        return this;
    }

    /**
     * Builds and returns the configured {@link Bugemon} instance.
     *
     * @throws IllegalStateException
     *             if no {@code name} was provided
     */
    public Bugemon build() {
        Bugemon bugemon = new Bugemon();

        // NOTE: name has no default value
        bugemon.name = this.name.orElseThrow(() -> new IllegalStateException("Bugemon name must be provided"));

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
