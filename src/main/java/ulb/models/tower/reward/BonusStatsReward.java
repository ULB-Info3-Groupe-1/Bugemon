package ulb.models.tower.reward;

import ulb.models.player.BonusStats;

public class BonusStatsReward implements Reward {
  private final BonusStats bonus;

  public BonusStatsReward(BonusStats bonus) {
    this.bonus = bonus;
  }

  public BonusStats getBonus() {
    return this.bonus;
  }
}
