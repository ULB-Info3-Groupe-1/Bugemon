package ulb.models.level_up;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ulb.common.dto.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO {
    private static final Random RANDOM = new Random();

    static final int NUM_UPGRADES = 3;
    static final int TARGET_POINTS = 10;

    private Bugemon bugemon;
    private List<Upgrade> upgrades;

    enum Stat {
        HP, ATTACK, DEFENSE, INITIATIVE,
    }

    public LevelUp(Bugemon bugemon) {
        this.bugemon = bugemon;
        this.upgrades = IntStream.range(0, NUM_UPGRADES).mapToObj(i -> this.generateRandomUpgrade()).toList();
    }

    public int numUpgrades() {
        return this.upgrades.size();
    }

    /**
     * Distributes 10 points randomly across HP, Attack, Defense, Initiative. HP and Initiative are scaled ×2; Attack
     * and Defense are face value.
     */
    private Upgrade generateRandomUpgrade() {
        int hp = 0;
        int attack = 0;
        int defense = 0;
        int initiative = 0;

        for (int i = 0; i < TARGET_POINTS; i++) {
            switch (this.randomStat()) {
                case HP -> hp++;
                case ATTACK -> attack++;
                case DEFENSE -> defense++;
                case INITIATIVE -> initiative++;
                default -> throw new RuntimeException("unknown stat");
            }
        }
        return new Upgrade(hp * 2, attack, defense, initiative * 2);
    }

    private Stat randomStat() {
        return Stat.values()[RANDOM.nextInt(Stat.values().length)];
    }

    @Override
    public Upgrade get(int idx) {
        if (idx > this.numUpgrades()) {
            throw new IndexOutOfBoundsException("attempted to get an upgrade out of bounds");
        }

        return this.upgrades.get(idx);
    }

    @Override
    public Bugemon getBugemon() {
        return this.bugemon;
    }

    public Iterable<Upgrade> upgrades() {
        return this.upgrades;
    }
}
