package ulb.views.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.BugemonDTO;
import ulb.controllers.combat.ManualCombatController;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public class ManualCombatView extends CombatView {

    private ManualCombatController controller;
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
    }

    @Override
    public void initCombatMode() {
        showMainMenu();
    }

    /**
     * set the controller for this view.
     * @param controller the controller to set for this view
     */
    public void setController(ManualCombatController controller) {
        this.controller = controller;
        
        // actionMenuView.setAction1Handler(controller::showAttackMenu);
        // actionMenuView.setAction2Handler(controller::showSwitchPanel);
        // actionMenuView.setAction3Handler(controller::showItemPanel);
        // actionMenuView.setAction4Handler(controller::handleSurrender);
    }

    /**
     * show the main action menu.
     */
    public void showMainMenu() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    /**
     * show the attack menu.
     */
    public void showAttackMenu(/*TODO: add attack dto */) {
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
