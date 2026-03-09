package ulb.views.combat;

import java.io.IOException;

public class AutomaticCombatView extends CombatView {
    public AutomaticCombatView() throws IOException {
        super();
        this.initCombatMode();
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
}
