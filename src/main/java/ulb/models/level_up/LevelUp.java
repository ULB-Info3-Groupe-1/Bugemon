package ulb.models.level_up;

import java.util.List;
import java.util.Random;

import ulb.common.dto.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO {
    private static final Random RANDOM = new Random();

    private Bugemon bugemon;
    private List<Upgrade> choices;

    public LevelUp(Bugemon bugemon) {
        this.bugemon = bugemon;
        this.choices = List.of(this.generateRandomChoice(), this.generateRandomChoice(), this.generateRandomChoice());
    }

    /**
     * Distributes 10 points randomly across HP, Attack, Defense, Initiative.
     * HP and Initiative are scaled ×2; Attack and Defense are face value.
     */
    private Upgrade generateRandomChoice() {
        int hp = 0;
        int attack = 0;
        int defense = 0;
        int initiative = 0;

        for (int i = 0; i < 10; i++) {
            int choice = RANDOM.nextInt(4); // 0: HP, 1: Attack, 2: Defense, 3: Initiative

            switch (choice) {
                case 0 -> hp++;
                case 1 -> attack++;
                case 2 -> defense++;
                case 3 -> initiative++;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            }
        }
        return new Upgrade(hp * 2, attack, defense, initiative * 2);
    }

    @Override
    public List<Upgrade> getChoices() {
        return this.choices;
    }

    @Override
    public Bugemon getBugemon() {
        return this.bugemon;
    }
}
