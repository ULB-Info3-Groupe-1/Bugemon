package ulb.views;

import java.io.IOException;

import ulb.controllers.LevelUpController;

/**
 * LevelUpView
 *
 * View for the level up screen.
 */
public class LevelUpView extends View {

    private LevelUpController controller;

    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
        this.controller = null;
    }

    /**
     * Binds Level up view to its controller.
     *
     * @param controller controller handling level up
     */
    public void setController(LevelUpController controller) {
        this.controller = controller;
    }

}
