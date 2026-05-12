package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

import ulb.models.bugemon.exceptions.InvalidAttackCountException;

// TODO: check if we could make this become a record

/**
 * Represents a Bugemon.
 */
public record Bugemon(
        String id,
        String name,
        int hp,
        int attack,
        int defense,
        int initiative,
        BugemonType type,
        List<Attack> attacks,
        String spritePath,
        boolean isStarter,
        boolean isBoss) {

    public static final int ATTACKS_COUNT = 3;

    public Bugemon {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(attacks);

        // TODO: add checks for other fields (stats > 0, name/id not empty etc.)

        attacks = List.copyOf(attacks);
        this.checkAttacks(attacks);
    }

    public Bugemon(String id, String name, int hp, int attack, int defense, int initiative, BugemonType type,
            List<Attack> attacks, String spritePath, boolean isStarter) {
        this(id, name, hp, attack, defense, initiative, type, attacks, spritePath, isStarter, false);
    }

    public void checkAttacks(List<Attack> attacks) {
        if (attacks.size() != ATTACKS_COUNT) {
            throw new InvalidAttackCountException(ATTACKS_COUNT, attacks.size());
        }
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
