package ulb.controllers;

import ulb.models.bugemon.BugemonType;
import ulb.services.BugemonService;
import ulb.views.CreateBugemonView;
import ulb.views.ViewLoader;

public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private final BugemonService bugemonService;

    public CreateBugemonController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(CreateBugemonView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);

    }

    @Override
    public void onTypeSelected(BugemonType selectedType) {
        return;
    }

    @Override
    public void onSave(String bugemonName, double healthValue, double attackValue, double defenseValue,
            double initiativeValue) {
        return;
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }
}
