package ulb.models.reward;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.Upgrade;

public class StatReward extends Reward {

    private final Upgrade upgrade;

    public StatReward(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return this.upgrade;
    }

    @Override
    public void applyReward(Bugemon target) {
        target.applyUpgrade(this.upgrade);
    }

    @Override
    public String getSummary() {
        return "Bonus stat : " + this.upgrade.toString();
    }
}
