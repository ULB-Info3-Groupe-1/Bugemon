package bugemon.common.models.tower.reward;

import bugemon.common.models.player.BonusStats;

/**
 * A {@link Reward} that applies a {@link bugemon.common.models.player.BonusStats} increment to the player's Bugemons.
 */
public class BonusStatsReward implements Reward {
    private final BonusStats bonus;

    public BonusStatsReward(BonusStats bonus) {
        this.bonus = bonus;
    }

    public BonusStats getBonus() {
        return this.bonus;
    }
}
