package ulb.models.level_up;

import java.util.List;
import java.util.Random;

import ulb.common.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO{
    
    // Attributes
    private Bugemon bugemon;
    private List<Choice> choices;

    public LevelUp(Bugemon bugemon){
        this.bugemon = bugemon;
        this.choices = List.of(generateRandomChoice(), generateRandomChoice(), generateRandomChoice());
    }


    private Choice generateRandomChoice() {
        Random rand = new Random();
        int hp = 0, attack = 0, defense = 0, initiative = 0;
        for (int i = 0; i < 10; i++) {
            int choice = rand.nextInt(4); // 0: HP, 1: Attack, 2: Defense, 3: Initiative

            switch(choice) {
                case 0 -> hp++;
                case 1 -> attack++;
                case 2 -> defense++;
                case 3 -> initiative++;
            }
        }
        return new Choice(hp*2, attack, defense, initiative*2);
    }

    @Override
    public List<Choice> getChoices() {
        return this.choices;
    }
    
    @Override
    public Bugemon getBugemon() {
        return this.bugemon;
    }
}
