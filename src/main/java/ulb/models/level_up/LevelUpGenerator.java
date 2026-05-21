package ulb.models.level_up;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ulb.Configuration;
import ulb.models.player.BonusStats;

public class LevelUpGenerator {
    enum Stat {
        HP,
        ATTACK,
        DEFENSE,
        INITIATIVE,
    }

    private final Random random;

    public LevelUpGenerator(Random random) {
        this.random = random;
    }

    public List<BonusStats> generateOptions() {
        return IntStream.range(0, Configuration.Game.NUM_BONUS_PER_LEVEL_UP).mapToObj(i -> this.generateBonusStats())
                .toList();
    }

    private BonusStats generateBonusStats() {
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
