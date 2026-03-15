package ulb.fx_controllers.combat;

import javafx.scene.control.Button;

import ulb.common.Efficiency;
import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.services.CombatService;

public class AttackActionMenu extends ActionMenuView {
    private ManualCombatController controller;

    public AttackActionMenu() {
        super();
        this.action4.setText("Retour");

        this.action1.getStyleClass().add("attack");
        this.action2.getStyleClass().add("attack");
        this.action3.getStyleClass().add("attack");
    }

    /**
     * Set the controller for the AttackActionMenu and define the action for the "Back" button to
     * show the main action menu
     */
    public void setController(ManualCombatController controller) {
        this.controller = controller;
        this.action4.setOnAction(e -> controller.showMainActionMenu());
    }

    /**
     * Set the text and action for the attack buttons based on the list of available attacks for the
     * player's current Bugemon, showing effectiveness against the opponent's type
     *
     * @param attack1 the first attack to display
     * @param attack2 the second attack to display
     * @param attack3 the third attack to display
     * @param opponentType the type of the opponent's current Bugemon
     */
    public void setAttacks(Attack attack1, Attack attack2, Attack attack3,
                           BugemonType opponentType) {
        setupAttackButton(this.action1, attack1, opponentType);
        setupAttackButton(this.action2, attack2, opponentType);
        setupAttackButton(this.action3, attack3, opponentType);
    }

    /**
     * Configure a single attack button with the given attack's properties and effectiveness display
     *
     * @param button the button to configure
     * @param attack the attack to display
     * @param opponentType the type of the opponent's Bugemon for effectiveness calculation
     */
    private void setupAttackButton(Button button, Attack attack, BugemonType opponentType) {
        button.getStyleClass().clear();
        button.getStyleClass().add("action-button");
        button.getStyleClass().add("attack-" + attack.getType().toString());

        Efficiency efficiency = CombatService.compareBugemonType(attack.getType(), opponentType);
        button.setText(attack.getName() + "\n" + efficiency.toString());

        button.setOnAction(e -> this.controller.playerAttack(attack));
    }
}
