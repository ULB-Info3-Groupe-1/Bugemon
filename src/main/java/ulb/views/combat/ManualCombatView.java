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
        this.controller = controller;
        this.mainActionMenu.setController(this.controller);
        this.attackActionMenu.setController(this.controller);
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

    /**
     * show the switch menu, which is the bugemon team view. The player can click on a bugemon to switch to it.
     * @param bugemonList the list of bugemons in the player's team to be displayed in the switch menu
     */
    public void showSwitchMenu(List<BugemonDTO> bugemonList) {
        this.bugemonTeamPane.setVisible(true);
        this.bugemonTeamPane.setManaged(true);
        this.bugemonTeamView.setOnClickCallback(bugemon -> {
            if (bugemon != null) {
                this.controller.switchBugemon(bugemon.getId());
                hideSwitchPanel();
                showMainActionMenu();
            }
        });
        this.bugemonTeamView.showTeam(bugemonList);
    }

    /**
     * hide the switch menu, which is the bugemon team view.
     */
    public void hideSwitchPanel() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    /**
     * hide all action menus (main action menu, attack menu, switch menu)
     */
    public void hideAllActionMenus() {
        this.actionMenuView.getChildren().clear();
    }

    /**
     * Show the screen of the combat debut, which is the main action menu and hide the switch panel
     */
    public void showScreenDebutCombat() {
        showMainActionMenu();
        hideSwitchPanel();
    }
}
