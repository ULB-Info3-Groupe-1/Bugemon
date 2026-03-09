package ulb.controllers;

import java.io.IOException;
import java.util.List;

import ulb.common.LevelUpDTO;
import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUp;
import ulb.views.LevelUpView;

public class LevelUpController extends Controller<LevelUpView> {

    LevelUp levelUp;

    public LevelUpController(MetaController metaController) throws IOException {
        super(metaController, new LevelUpView());
        this.view.setController(this);

        Bugemon bugemon = new Bugemon.Builder()
        .id("1")
        .build();

        this.levelUp = bugemon.levelUp();
        this.view.setLevelUp(this.levelUp);
    }

    /**
     * @param optionIdx the index of the chosen option
     */
    public void chooseOption(int optionIdx) {
        List<Choice> choices = this.levelUp.getChoices();
        Choice choice = choices.get(optionIdx);
        this.levelUp.getBugemon().applyChoice(choice);
        System.out.println("Chosen option: " + choice.toString());
    }

    public void setLevelUp(LevelUp levelUp) {
        this.levelUp = levelUp;
        this.view.setLevelUp(levelUp);
    }

}
