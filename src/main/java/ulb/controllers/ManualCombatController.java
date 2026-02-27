package ulb.controllers;

import java.io.IOException;

import ulb.views.combat.ManualCombatView;

public class ManualCombatController extends CombatController<ManualCombatView> {
    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());

        this.view.showDialog("Oh nice a fucking hardcoded thing...", "fuck yeah");

        this.view.setController(this);
    }
}
