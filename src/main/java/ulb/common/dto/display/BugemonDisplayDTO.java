package ulb.common.dto.display;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;

/**
 * Display snapshot of a player-owned Bugemon, combining the immutable base stats stored in {@link Bugemon} with the
 * per-run bonus stats and progression data.
 *
 * <p>
 * Views should call the computed accessors ({@link #getMaxHp()}, {@link #getDefense()}, {@link #getAttack()},
 * {@link #getInitiative()}) rather than reading {@code base} directly, so that bonuses are always included in the
 * displayed values.
 *
 * @param base
 *            the static Bugemon template containing base stats and metadata
 * @param bonusDefense
 *            flat bonus added to the base defense stat
 * @param bonusAttackPower
 *            flat bonus added to the base attack stat
 * @param bonusInitiative
 *            flat bonus added to the base initiative stat
 * @param bonusMaxHp
 *            flat bonus added to the base max-HP stat
 * @param xp
 *            accumulated experience points for this Bugemon
 * @param level
 *            current level of this Bugemon
 * @param attacks
 *            ordered list of attacks currently known by this Bugemon
 */
public record BugemonDisplayDTO(Bugemon base, int bonusDefense, int bonusAttackPower, int bonusInitiative,
        int bonusMaxHp, int xp, int level, List<Attack> attacks) {

    public int getMaxHp() {
        return this.base().hp() + this.bonusMaxHp();
    }

    public int getDefense() {
        return this.base().defense() + this.bonusDefense();
    }

    public int getAttack() {
        return this.base().attack() + this.bonusAttackPower();
    }

    public int getInitiative() {
        return this.base().initiative() + this.bonusInitiative();
    }

    public String getName() {
        return this.base().name();
    }

    public ElementType getType() {
        return this.base().type();
    }

    public String getSpritePath() {
        return this.base().spritePath();
    }
}
