package ulb.models.reward;

import ulb.models.bugemon.Bugemon;

public abstract class Reward {

    public abstract String getSummary();

    public abstract void applyReward(Bugemon target);
}
