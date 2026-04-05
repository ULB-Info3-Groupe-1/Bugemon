package ulb.controllers;

import java.io.IOException;

import ulb.models.bugemon.BugemonType;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.CreateBugemonView;
import ulb.views.ViewLoader;

public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private final BugemonService bugemonService;

    public CreateBugemonController(MetaController metaController, BugemonService bugemonService) throws IOException {
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
}
