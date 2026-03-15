package ulb.fx_controllers.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public class ManualCombatFXController extends CombatFXController {
    private final MainActionMenu mainActionMenu;
    private final AttackActionMenu attackActionMenu;

    /**
     * Constructor for ManualCombatView.
     * @throws IOException
     */
    public ManualCombatFXController(ManualCombatController controller) throws IOException {
        super(controller);
        this.mainActionMenu = new MainActionMenu();
        this.attackActionMenu = new AttackActionMenu();
        this.initCombatMode();
    }

    @Override
    public void initCombatMode() {
        showMainActionMenu();
    }

    /**
     * show the main action menu.
     */
    public void showMainActionMenu() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    /**
     * show the attack menu.
     * @param attackNames the list of attacks available to the player's current Bugemon
     * @param opponentType the type of the opponent's current Bugemon
     */
    public void showAttackMenu(List<Attack> attackNames, BugemonType opponentType) {
        this.attackActionMenu.setAttacks(attackNames.get(0), attackNames.get(1), attackNames.get(2),
                                         opponentType);
        this.actionMenuView.getChildren().setAll(attackActionMenu);
    }

    /**
     * show the switch menu, which is the bugemon team view. The player can click on a bugemon to
     * switch to it.
     * @param bugemonList the list of bugemons in the player's team to be displayed in the switch
     *         menu
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
