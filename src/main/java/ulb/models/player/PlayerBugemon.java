package ulb.models.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ulb.common.dto.display.BugemonDisplayDTO;
import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.damage.Efficiency;
import ulb.models.player.exceptions.IllegalAttackReplacementException;

/**
 * Persistent player-owned Bugemon enriched with leveling, XP, individually accumulated stat bonuses, and a customisable
 * attack set.
 *
 * <p>
 * Base stats come from the underlying {@link ulb.models.bugemon.Bugemon} definition; {@link BonusStats} are added on
 * top. The four attacks in {@code currentAttacks} may differ from the base Bugemon's default attacks after the player
 * replaces them via {@link #replaceAttack(Attack, Attack)}.
 *
 * <p>
 * Use {@link #from(Bugemon, PlayerBugemonDTO)} to reconstruct an instance from persisted data, and
 * {@link #toDTO(String)} / {@link #toDisplayDTO()} to convert back for persistence or UI display.
 */
public class PlayerBugemon {
    private final Bugemon base;
    private int level;
    private int xp;
    private BonusStats bonusStats;
    private final List<Attack> currentAttacks;

    /**
     * Creates a fresh level-1, zero-XP {@link PlayerBugemon} with default bonus stats and the Bugemon's own attacks.
     *
     * @param base
     *            the underlying Bugemon definition; must not be {@code null}
     */
    public PlayerBugemon(Bugemon base) {
        this(base, 1, 0, new BonusStats(), base.attacks());
    }

    /**
     * Reconstructs a {@link PlayerBugemon} from a persistence DTO.
     *
     * @param base
     *            the underlying Bugemon definition
     * @param dto
     *            the persisted data record to restore from
     * @return a fully initialised {@link PlayerBugemon} with the saved level, XP, bonuses, and attacks
     */
    public static PlayerBugemon from(Bugemon base, PlayerBugemonDTO dto) {
        BonusStats bonusStats = new BonusStats(dto.bonusMaxHp(), dto.bonusAttackPower(), dto.bonusDefense(),
                dto.bonusInitiative());
        return new PlayerBugemon(base, dto.level(), dto.xp(), bonusStats, dto.attacks());
    }

    /**
     * Full constructor used internally and by {@link #from(Bugemon, PlayerBugemonDTO)}.
     *
     * @param base
     *            the underlying Bugemon definition; must not be {@code null}
     * @param level
     *            the starting level; must be at least 1
     * @param xp
     *            the current XP within the current level
     * @param bonusStats
     *            accumulated stat bonuses; must not be {@code null}
     * @param currentAttacks
     *            the list of active attacks; validated by {@link ulb.models.bugemon.Bugemon#checkAttacks}
     */
    public PlayerBugemon(Bugemon base, int level, int xp, BonusStats bonusStats, List<Attack> currentAttacks) {
        this.base = Objects.requireNonNull(base);

        this.level = level;
        this.xp = xp;
        this.bonusStats = bonusStats;

        Bugemon.checkAttacks(currentAttacks);
        this.currentAttacks = new ArrayList<>(currentAttacks);
    }

    public String getName() {
        return this.base.name();
    }

    public ElementType getType() {
        return this.base.type();
    }

    public int getMaxHp() {
        return this.base.hp() + this.bonusStats.getBonusHp();
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }

    public int getAttack() {
        return this.base.attack() + this.bonusStats.getBonusAttack();
    }

    public int getDefense() {
        return this.base.defense() + this.bonusStats.getBonusDefense();
    }

    public int getInitiative() {
        return this.base.initiative() + this.bonusStats.getBonusInitiative();
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.currentAttacks);
    }

    /**
     * Returns the total XP needed to advance from the current level to the next.
     *
     * @return the XP threshold for the next level-up
     */
    public int getXpToNextLevel() {
        return 50 + 50 * (this.level - 1);
    }

    /**
     * Adds {@code xpToAdd} XP and applies any resulting level-ups.
     *
     * @param xpToAdd
     *            the amount of XP to grant; must be non-negative
     * @return the number of levels gained
     * @throws IllegalArgumentException
     *             if {@code xpToAdd} is negative
     */
    public int addXp(int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("amount of xp to add must be non-negative");
        }

        int numLevelUps = 0;

        this.xp += xpToAdd;

        while (this.xp >= this.getXpToNextLevel()) {
            this.xp -= this.getXpToNextLevel();
            numLevelUps++;
            this.level++;
        }

        return numLevelUps;
    }

    /**
     * Merges {@code bonus} into this Bugemon's accumulated stat bonuses.
     *
     * @param bonus
     *            the bonus increments to apply; must not be {@code null}
     */
    public void applyBonus(BonusStats bonus) {
        this.bonusStats.add(bonus);
    }

    public String getSpritePath() {
        return this.base.spritePath();
    }

    public List<Attack> getAttackList() {
        return this.currentAttacks;
    }

    /**
     * Returns the fraction of XP accumulated towards the next level, in the range {@code [0.0, 1.0)}.
     *
     * @return XP progress as a ratio of current XP to {@link #getXpToNextLevel()}
     */
    public double getXpProgress() {
        return (double) this.xp / this.getXpToNextLevel();
    }

    /**
     * Replaces {@code oldAttack} in this Bugemon's active attack list with {@code newAttack}.
     *
     * <p>
     * The replacement is rejected if:
     * <ul>
     * <li>the new attack's element type is one this Bugemon is weak against (efficiency below normal), or</li>
     * <li>{@code oldAttack} is not currently in the attack list.</li>
     * </ul>
     *
     * @param oldAttack
     *            the attack to replace; must be present in the current attack list
     * @param newAttack
     *            the attack to put in its place
     * @return {@code true} on success
     * @throws IllegalAttackReplacementException
     *             if the replacement is not permitted
     */
    public boolean replaceAttack(Attack oldAttack, Attack newAttack) throws IllegalAttackReplacementException {
        if (!Efficiency.preview(newAttack.type(), this.getType()).isAtLeastNormal()) {
            throw new IllegalAttackReplacementException("bugemon is weak against the type of the given attack");
        }

        int index = this.currentAttacks.indexOf(oldAttack);
        if (index == -1) {
            throw new IllegalAttackReplacementException(
                    "attempted to replace an attack that the bugemon does not have");
        }
        this.currentAttacks.set(index, newAttack);

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PlayerBugemon other = (PlayerBugemon) obj;
        return this.base.equals(other.base) && this.level == other.level && this.xp == other.xp
                && this.bonusStats.equals(other.bonusStats) && this.currentAttacks.equals(other.currentAttacks);
    }

    @Override
    public int hashCode() {
        return this.base.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s Nv.%d XP.%d", this.base.name(), this.level, this.xp);
    }

    /**
     * Converts this instance to a {@link PlayerBugemonDTO} suitable for database persistence.
     *
     * @param playerName
     *            the owning player's name, stored as a foreign key in the DTO
     * @return a new {@link PlayerBugemonDTO} capturing all mutable state
     */
    public PlayerBugemonDTO toDTO(String playerName) {
        return new PlayerBugemonDTO(playerName, this.base.name(), this.bonusStats.getBonusDefense(),
                this.bonusStats.getBonusAttack(), this.bonusStats.getBonusInitiative(), this.bonusStats.getBonusHp(),
                this.xp, this.level, Collections.unmodifiableList(this.currentAttacks));
    }

    /**
     * Converts this instance to a {@link BugemonDisplayDTO} for UI display purposes.
     *
     * @return a new {@link BugemonDisplayDTO} capturing base stats, bonuses, XP, level, and current attacks
     */
    public BugemonDisplayDTO toDisplayDTO() {
        return new BugemonDisplayDTO(this.base, this.bonusStats.getBonusDefense(), this.bonusStats.getBonusAttack(),
                this.bonusStats.getBonusInitiative(), this.bonusStats.getBonusHp(), this.xp, this.level,
                this.currentAttacks);
    }
}
