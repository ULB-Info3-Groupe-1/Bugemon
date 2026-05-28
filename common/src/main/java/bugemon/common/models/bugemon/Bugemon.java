package bugemon.common.models.bugemon;

import java.util.List;
import java.util.Objects;

import bugemon.common.Configuration;
import bugemon.common.models.bugemon.exceptions.InvalidAttackCountException;

/**
 * Immutable record representing the base definition of a Bugemon species.
 *
 * <p>
 * A {@code Bugemon} describes the template of a species: its name, base stats, elemental type, fixed move-set and
 * sprite. It is <b>not</b> a live battle participant; runtime state such as current HP is managed by
 * {@link bugemon.common.models.run.RunBugemon} and ownership/progression by {@link bugemon.common.models.player.PlayerBugemon}.
 *
 * <p>
 * The compact constructor enforces all invariants: name must be non-blank, {@code hp} must be non-negative, every other
 * stat must be strictly positive, and the attack list must contain exactly {@link #ATTACKS_COUNT} entries. Attacks are
 * defensively copied to ensure immutability.
 *
 * <p>
 * Equality is based solely on {@code name}, which is treated as a unique species identifier.
 *
 * @param name
 *            unique species name
 * @param hp
 *            base hit-points (must be &ge; 0)
 * @param attack
 *            base attack power (must be &gt; 0)
 * @param defense
 *            base defense value (must be &gt; 0)
 * @param initiative
 *            base initiative (turn-order priority, must be &gt; 0)
 * @param type
 *            elemental type of the species
 * @param attacks
 *            fixed list of {@link Attack}s (exactly {@link #ATTACKS_COUNT} required)
 * @param spritePath
 *            classpath-relative path to the sprite image
 * @param isStarter
 *            {@code true} if this species can be chosen as a starter
 * @param isBoss
 *            {@code true} if this species is a boss-tier opponent
 */
public record Bugemon(String name, int hp, int attack, int defense, int initiative, ElementType type,
        List<Attack> attacks, String spritePath, boolean isStarter, boolean isBoss) {

    public static final int ATTACKS_COUNT = Configuration.Game.ATTACKS_COUNT;

    public Bugemon {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(attacks);

        if (name.isBlank() || name.isEmpty()) {
            throw new IllegalArgumentException("Bugemon's name cannot be empty");
        }
        checkHp(hp);
        if (attack <= 0 || defense <= 0 || initiative <= 0) {
            throw new IllegalArgumentException("Stats must be strictly positive");
        }

        checkAttacks(attacks);
        attacks = List.copyOf(attacks);
    }

    /**
     * Validates that the given list contains exactly {@link #ATTACKS_COUNT} attacks.
     *
     * @param attacks
     *            the list to validate
     * @throws InvalidAttackCountException
     *             if the list size does not equal {@link #ATTACKS_COUNT}
     */
    public static void checkAttacks(List<Attack> attacks) {
        if (attacks.size() != ATTACKS_COUNT) {
            throw new InvalidAttackCountException(ATTACKS_COUNT, attacks.size());
        }
    }

    /**
     * Validates that the given HP value is non-negative.
     *
     * @param hp
     *            the HP value to check
     * @throws IllegalArgumentException
     *             if {@code hp} is negative
     */
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
