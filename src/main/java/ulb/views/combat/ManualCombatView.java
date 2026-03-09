package ulb.views.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.BugemonDTO;
import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public class ManualCombatView extends CombatView {

    private MainActionMenu         mainActionMenu;
    private AttackActionMenu       attackActionMenu;

    /**
     * Constructor for ManualCombatView.
     * @throws IOException 
     */
    public ManualCombatView() throws IOException {
        super();
        this.mainActionMenu = new MainActionMenu();
        this.attackActionMenu = new AttackActionMenu();
        this.initCombatMode();
    }

    @Override
    public void initCombatMode() {
        showMainActionMenu();
    }

    /**
     * set the controller for this view.
     * @param controller the controller to set for this view
     */
    public void setController(ManualCombatController controller) {
        this.mainActionMenu.setController(controller);
        this.attackActionMenu.setController(controller);
    }

    /**
     * show the main action menu.
     */
    public void showMainActionMenu() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    /**
     * show the attack menu.
     */
    public void showAttackMenu(List<Attack> attackNames) {
        this.attackActionMenu.setAttacks(attackNames.get(0), attackNames.get(1), attackNames.get(2));
        this.actionMenuView.getChildren().setAll(attackActionMenu);
    }

    public void showSwitchPanel(List<BugemonDTO> bugemonList) {
        this.bugemonTeamPane.setVisible(true);
        this.bugemonTeamPane.setManaged(true);

        this.bugemonTeamView.showTeam(bugemonList);
    }

    public void hideSwitchPanel() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    public void showItemPanel() {
        // TODO:
    }
}
