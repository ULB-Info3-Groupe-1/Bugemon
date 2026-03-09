package ulb.controllers;

import java.io.IOException;
import java.util.List;

import ulb.common.LevelUpDTO;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUp;
import ulb.views.LevelUpView;

public class LevelUpController extends Controller<LevelUpView> {

    List<LevelUp> levelUps;
    int currentIdx;

    public LevelUpController(MetaController metaController) throws IOException {
        super(metaController, new LevelUpView());
        this.view.setController(this);
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

    public void cont(){
        this.currentIdx++;
        if (this.currentIdx < this.levelUps.size()) {
            this.view.setLevelUp(this.levelUps.get(this.currentIdx));
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            //TODO: see with client, this.metaController.resetTeam();
        }
    }

}
