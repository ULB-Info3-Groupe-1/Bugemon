package ulb.models.reward;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

public class AttackReward implements Reward {

    private final Attack newAttack;
    private int indexToReplace;

    public AttackReward(Attack newAttack) {
        this.newAttack = newAttack;
        this.indexToReplace = -1;
    }

    public void setIndexToReplace(int index) {
        this.indexToReplace = index;
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.ATTACK;
    }

    @Override
    public void applyReward(Bugemon target) {
        if (this.indexToReplace == -1)
            throw new IllegalStateException("Cannot apply reward: No attack selected for replacement");
        target.learnAttack(this.indexToReplace, this.newAttack);
    }

    @Override
    public String getSummary() {
        return "Bonus : Nouvelle attaque :" + this.newAttack.toString();
    }
}
