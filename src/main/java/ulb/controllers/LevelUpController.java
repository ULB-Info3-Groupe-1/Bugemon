package ulb.controllers;

import java.io.IOException;

import ulb.views.LevelUpView;

public class LevelUpController extends Controller<LevelUpView> {

    public LevelUpController(MetaController metaController) throws IOException {
        super(metaController, new LevelUpView());
        this.view.setController(this);
    }

    /**
     * @param optionIdx the index of the chosen option
     */
    public void chooseOption(int optionIdx) {
        // TODO: impl
    }

}
