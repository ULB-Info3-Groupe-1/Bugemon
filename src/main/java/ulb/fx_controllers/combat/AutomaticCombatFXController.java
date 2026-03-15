package ulb.fx_controllers.combat;

import ulb.controllers.combat.AutomaticCombatController;

public class AutomaticCombatFXController extends CombatFXController {

    public AutomaticCombatFXController(AutomaticCombatController controller) {
        super(controller);
        this.initCombatMode();
    }

    /**
     * Initialize the combat mode for automatic combat.
     * Hides the action menu and team pane since they are not needed in automatic combat.
     */
    @Override
    public void initCombatMode() {
        this.actionMenuComponent.setVisible(false);
        this.actionMenuComponent.setManaged(false);
        hideBugemonTeamPane();

    }
}
