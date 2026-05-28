package bugemon.common.models.tower.reward;

import bugemon.common.models.bugemon.Attack;

/**
 * A {@link Reward} that grants the player a new {@link bugemon.common.models.bugemon.Attack} to assign to one of their
 * Bugemons.
 */
public class AttackReward implements Reward {
    private final Attack attack;

    public AttackReward(Attack attack) {
        this.attack = attack;
    }

    public Attack getAttack() {
        return this.attack;
    }
}
