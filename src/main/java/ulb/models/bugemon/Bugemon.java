package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

import ulb.Configuration;
import ulb.models.bugemon.exceptions.InvalidAttackCountException;

/**
 * Represents a Bugemon.
 */
public record Bugemon(String name, int hp, int attack, int defense, int initiative, ElementType type,
        List<Attack> attacks, String spritePath, boolean isStarter, boolean isBoss) {

    public static final int ATTACKS_COUNT = Configuration.Game.ATTACKS_COUNT;

    public Bugemon {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(attacks);

        // TODO: add checks for other fields (stats > 0, name/id not empty etc.)

        Objects.requireNonNull(attacks);
        checkAttacks(attacks);
        attacks = List.copyOf(attacks);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public Bugemon(String name, int hp, int attack, int defense, int initiative, ElementType type, List<Attack> attacks,
            String spritePath, boolean isStarter) {
        this(name, hp, attack, defense, initiative, type, attacks, spritePath, isStarter, false);
    }

    public static void checkAttacks(List<Attack> attacks) {
        if (attacks.size() != ATTACKS_COUNT) {
            throw new InvalidAttackCountException(ATTACKS_COUNT, attacks.size());
        }
    }

    public static void checkHp(int hp) {
        if (hp < 0) {
            throw new IllegalArgumentException("Bugemon's current hp must be non-negative");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bugemon other = (Bugemon) obj;
        return this.name.equals(other.name);
        // NOTE: no need to check equality for other member as id is supposed to be
        // unique
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] HP:%d ATK:%d DEF:%d INIT:%d", this.name, this.type, this.hp, this.attack,
                this.defense, this.initiative);
    }
}
