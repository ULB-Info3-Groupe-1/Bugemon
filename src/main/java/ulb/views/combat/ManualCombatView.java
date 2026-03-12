package ulb.views.combat;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public class ManualCombatView extends CombatView {
    private Consumer<String> onSwitchBugemon;

    private MainActionMenu mainActionMenu;
    private AttackActionMenu attackActionMenu;

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

    /**
     * Initialize the combat mode by showing the main action menu.
     */
    @Override
    public void initCombatMode() {
        showMainActionMenu();
    }

    /**
     * Set the action to perform when the attack button is clicked.
     * @param action the action to perform
     */
    public void setOnShowAttackMenu(Runnable action) {
        this.mainActionMenu.setOnAttack(action);
    }

    /**
     * Set the action to perform when the switch button is clicked.
     * @param action the action to perform
     */
    public void setOnShowSwitchMenu(Runnable action) {
        this.mainActionMenu.setOnSwitch(action);
    }

    /**
     * Set the action to perform when the surrender button is clicked.
     * @param action the action to perform
     */
    public void setOnSurrender(Runnable action) {
        this.mainActionMenu.setOnSurrender(action);
    }

    /**
     * Set the action to perform when the back button in the attack menu is clicked.
     * @param action the action to perform
     */
    public void setOnBackToMainActionMenu(Runnable action) {
        this.attackActionMenu.setOnBack(action);
    }

    /**
     * Set the callback to be invoked when an attack is selected in the attack menu.
     * @param onAttackSelected the Consumer that will handle the selected Attack
     */
    public void setOnAttackSelected(Consumer<Attack> onAttackSelected) {
        this.attackActionMenu.setOnAttackSelected(onAttackSelected);
    }

    /**
     * Set the callback to be invoked when a Bugemon is selected in the switch menu.
     * @param onSwitchBugemon the Consumer that will handle the selected Bugemon's id for switching
     */
    public void setOnSwitchBugemon(Consumer<String> onSwitchBugemon) {
        this.onSwitchBugemon = onSwitchBugemon;
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
            if (bugemon != null && this.onSwitchBugemon != null) {
                this.onSwitchBugemon.accept(bugemon.getId());
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
