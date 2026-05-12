package ulb.models.bugemon;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ulb.models.bugemon.exceptions.InvalidAttackCountException;

// TODO: check if we could make this become a record

/**
 * Represents a Bugemon.
 */ 
public class Bugemon {
    public static final int ATTACKS_COUNT = 3;

    private final String id;
    private final String name;
    private final int hp;
    private final int attack;
    private final int defense;
    private final int initiative;
    private final BugemonType type;
    private final List<Attack> attacks;
    private final String spritePath;
    private final boolean isStarter;
    private final boolean isBoss;

    public Bugemon(String id, String name, int hp, int attack, int defense, int initiative, BugemonType type,
            List<Attack> attacks, String spritePath, boolean isStarter) {
        this(id, name, hp, attack, defense, initiative, type, attacks, spritePath, isStarter, false);
    }

    public Bugemon(String id, String name, int hp, int attack, int defense, int initiative, BugemonType type,
            List<Attack> attacks, String spritePath, boolean isStarter, boolean isBoss) {
        if (attacks.size() != ATTACKS_COUNT) {
            throw new InvalidAttackCountException(ATTACKS_COUNT, attacks.size());
        }

        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.initiative = initiative;
        this.type = Objects.requireNonNull(type);
        this.attacks = List.copyOf(attacks); // TODO: do we really need a copy here?
        this.spritePath = spritePath;
        this.isStarter = isStarter;
        this.isBoss = isBoss;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getHp() {
        return this.hp;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public int getInitiative() {
        return this.initiative;
    }

    public BugemonType getType() {
        return this.type;
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.attacks);
    }

    public String getSpritePath() {
        return this.spritePath;
    }

    public boolean isStarter() {
        return this.isStarter;
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: decide which equals behavior we need: instanceof vs getclass
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bugemon other = (Bugemon) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] HP:%d ATK:%d DEF:%d INIT:%d", this.name, this.type, this.hp, this.attack,
                this.defense, this.initiative);
    }
}
