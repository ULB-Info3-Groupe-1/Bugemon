package bugemon.common.models.player;

import java.util.Random;

import bugemon.common.Configuration;

/**
 * Randomly allocates a fixed pool of stat points across HP, attack, defense, and initiative to produce a
 * {@link BonusStats} reward.
 *
 * <p>
 * The pool size and per-point gain values are read from {@link bugemon.common.Configuration.Game}. Inject a seeded
 * {@link java.util.Random} to make generation reproducible.
 */
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

    /**
     * Generates a new {@link BonusStats} by distributing {@link bugemon.common.Configuration.Game#NUM_POINTS_PER_BONUS} points
     * randomly across the four stat categories.
     *
     * @return a freshly generated {@link BonusStats} instance
     */
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
