package ulb.models.reward;

import ulb.models.bugemon.Bugemon;

public abstract class Reward {

    private final RewardType type;

    protected Reward(RewardType type) {
        this.type = type;
    }

    public RewardType getRewardType() {
        return this.type;
    }

    public abstract String getSummary();

    public abstract void applyReward(Bugemon target);
}
