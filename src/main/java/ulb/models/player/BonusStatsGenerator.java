package ulb.models.player;

import java.util.Random;

import ulb.Configuration;

public class BonusStatsGenerator {
    enum Stat {
        HP,
        ATTACK,
        DEFENSE,
        INITIATIVE,
    }

    private final Random random;

    public BonusStatsGenerator(Random random) {
        this.random = random;
    }

    public BonusStats generateBonusStats() {
        int hp = 0;
        int attack = 0;
        int defense = 0;
        int initiative = 0;

        for (int i = 0; i < Configuration.Game.NUM_POINTS_PER_BONUS; i++) {
            switch (this.randomStat()) {
                case HP -> hp++;
                case ATTACK -> attack++;
                case DEFENSE -> defense++;
                case INITIATIVE -> initiative++;
                default -> throw new IllegalStateException("unknown stat");
            }
        }
        return new BonusStats(hp * Configuration.Game.HP_GAIN_PER_POINT,
                attack * Configuration.Game.ATTACK_GAIN_PER_POINT, defense * Configuration.Game.DEFENSE_GAIN_PER_POINT,
                initiative * Configuration.Game.INITIATIVE_GAIN_PER_POINT);
    }

    private Stat randomStat() {
        return Stat.values()[this.random.nextInt(Stat.values().length)];
    }
}
