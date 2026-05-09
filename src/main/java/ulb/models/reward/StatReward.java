package ulb.models.reward;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.Upgrade;

public class StatReward implements Reward {

    private final Upgrade upgrade;

    public StatReward(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public void applyReward(Bugemon target) {
        target.applyUpgrade(this.upgrade);
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.STAT;
    }

    @Override
    public String getSummary() {
        return "Bonus stat: " + this.upgrade.toString();
    }
}
