package ulb.controllers;

import java.util.List;

import ulb.controllers.MetaController.Window;
import ulb.fx_controllers.LevelUpView;
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUp;
import ulb.services.LevelUpService;

public class LevelUpController extends Controller {
    private final LevelUpService levelUpService;

    public LevelUpController(MetaController metaController, LevelUpService levelUpService){
        super(metaController);

        this.levelUpService = levelUpService;
    }

    /**
     * @param optionIdx the index of the chosen option
     */
    public void chooseOption(int optionIdx) {
        LevelUp levelUp = this.levelUps.get(this.currentIdx);
        List<Choice> choices = levelUp.getChoices();
        Choice choice = choices.get(optionIdx);
        levelUp.getBugemon().applyChoice(choice);
        this.cont();
    }

    public void setLevelUp(List<LevelUp> lvlsUp) {
        if (!lvlsUp.isEmpty()) {
            this.levelUps = lvlsUp;
            this.currentIdx = 0;
            this.view.setLevelUp(this.levelUps.get(this.currentIdx));
            this.metaController.switchTo(Window.LEVEL_UP);
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }

    public void cont() {
        this.currentIdx++;
        if (this.currentIdx < this.levelUps.size()) {
            this.view.setLevelUp(this.levelUps.get(this.currentIdx));
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            // TODO: see with client, this.metaController.resetTeam();
        }
    }
}
