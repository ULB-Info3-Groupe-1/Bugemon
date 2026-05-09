package ulb.models.reward;

import ulb.models.bugemon.Bugemon;

public interface Reward {

    RewardType getRewardType();

    String getSummary();

    void applyReward(Bugemon target);
}
