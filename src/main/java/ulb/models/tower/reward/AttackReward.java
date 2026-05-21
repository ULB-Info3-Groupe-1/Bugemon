package ulb.models.tower.reward;

import ulb.models.bugemon.Attack;

public class AttackReward implements Reward {
  private final Attack attack;

  public AttackReward(Attack attack) {
    this.attack = attack;
  }

  public Attack getAttack() {
    return this.attack;
  }
}
