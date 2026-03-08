package ulb.views.combat;

import java.io.IOException;

import ulb.controllers.combat.AutomaticCombatController;

public class AutomaticCombatView extends CombatView {
    private AutomaticCombatController controller;

    public AutomaticCombatView() throws IOException {
        super();
    }

    /**
     * Initialize the combat mode for automatic combat.
     * Hides the action menu and team pane since they are not needed in automatic combat.
     */
    @Override
    public void initCombatMode() {
        this.actionMenuView.setVisible(false);
        this.actionMenuView.setManaged(false);
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);   
    }

    /**
     * Set the controller for this view.
     * @param controller
     */
    public void setController(AutomaticCombatController controller) {
        this.controller = controller;
    }
}
