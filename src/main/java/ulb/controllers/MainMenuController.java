package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.MainMenuView;

public class MainMenuController extends Controller<MainMenuView> {

    public MainMenuController(MetaController metaController) throws IOException {
        super(metaController, new MainMenuView());
        this.view.setController(this);
    }

    public void createTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

}
