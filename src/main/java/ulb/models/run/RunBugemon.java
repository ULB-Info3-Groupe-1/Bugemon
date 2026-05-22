package ulb.models.run;

import java.util.Collections;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.player.BonusStats;
import ulb.models.player.PlayerBugemon;

/**
 * A run-scoped view of a {@link ulb.models.player.PlayerBugemon} that tracks mutable current HP during combat.
 *
 * <p>
 * Stat queries (attack, defense, etc.) and XP mutations are delegated to the underlying
 * {@link ulb.models.player.PlayerBugemon}. HP changes are local to this wrapper and do not affect the persisted Bugemon
 * until explicitly propagated by the caller.
 */
public class RunBugemon {
    private final PlayerBugemon playerBugemon;
    private int currentHp;

    /**
     * Creates a {@link RunBugemon} with current HP initialised to the Bugemon's maximum.
     *
     * @param playerBugemon
     *            the underlying persistent Bugemon; must not be {@code null}
     */
    public RunBugemon(PlayerBugemon playerBugemon) {
        this(playerBugemon, playerBugemon.getMaxHp());
    }

    /**
     * Creates a {@link RunBugemon} with an explicit starting HP value.
     *
     * @param playerBugemon
     *            the underlying persistent Bugemon
     * @param currentHp
     *            the HP to start with; must satisfy {@link ulb.models.bugemon.Bugemon#checkHp}
     */
    public RunBugemon(PlayerBugemon playerBugemon, int currentHp) {
        this.playerBugemon = playerBugemon;

        Bugemon.checkHp(currentHp);
        this.currentHp = currentHp;
    }

    public String getName() {
        return this.playerBugemon.getName();
    }

    public ElementType getType() {
        return this.playerBugemon.getType();
    }

    public boolean isKo() {
        return this.currentHp == 0;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return this.playerBugemon.getMaxHp();
    }

    public void restoreHpToMax() {
        this.currentHp = this.getMaxHp();
    }

    public int getAttack() {
        return this.playerBugemon.getAttack();
    }

    public int getDefense() {
        return this.playerBugemon.getDefense();
    }

    public int getInitiative() {
        return this.playerBugemon.getInitiative();
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.playerBugemon.getAttacks());
    }

    public double getXpProgress() {
        return this.playerBugemon.getXpProgress();
    }

    /**
     * Applies the given stat bonus to the underlying {@link ulb.models.player.PlayerBugemon} and resets current HP to
     * the new maximum.
     *
     * @param bonus
     *            the bonus stats to apply
     */
    public void applyBonus(BonusStats bonus) {
        this.playerBugemon.applyBonus(bonus);
        this.currentHp = this.getMaxHp();
    }

    public String getSpritePath() {
        return this.playerBugemon.getSpritePath();
    }

    public int getLevel() {
        return this.playerBugemon.getLevel();
    }

    public PlayerBugemon getPlayerBugemon() {
        return this.playerBugemon;
    }

    public int addXp(int xp) {
        return this.playerBugemon.addXp(xp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RunBugemon other = (RunBugemon) obj;
        return this.playerBugemon.equals(other.playerBugemon) && (this.currentHp == other.currentHp);
    }

    @Override
    public int hashCode() {
        return this.playerBugemon.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s CURRENT-HP:%d", this.getName(), this.currentHp);
    }
}
