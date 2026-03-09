package ulb.views.combat;

import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;
import ulb.views.ActionMenuView;

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
     * player's current Bugemon
     */
    public void setAttacks(Attack attack1, Attack attack2, Attack attack3) {
        this.action1.getStyleClass().clear();

        this.action1.getStyleClass().add("action-button");
        this.action1.setText(attack1.getName() + "\n"
                             + this.controller
                                       .isAttackEfficient(attack1.getType(),
                                                          this.controller.getOpponentBugemonType())
                                       .toString());

        this.action1.getStyleClass().add("attack-" + attack1.getType().toString());
        this.action1.setOnAction(e -> this.controller.playerAttack(attack1));

        this.action2.getStyleClass().clear();
        this.action2.getStyleClass().add("action-button");
        this.action2.setText(attack2.getName() + "\n"
                             + this.controller
                                       .isAttackEfficient(attack2.getType(),
                                                          this.controller.getOpponentBugemonType())
                                       .toString());

        this.action2.getStyleClass().add("attack-" + attack2.getType().toString());
        this.action2.setOnAction(e -> this.controller.playerAttack(attack2));

        this.action3.getStyleClass().clear();
        this.action3.getStyleClass().add("action-button");
        this.action3.setText(attack3.getName() + "\n"
                             + this.controller
                                       .isAttackEfficient(attack3.getType(),
                                                          this.controller.getOpponentBugemonType())
                                       .toString());

        this.action3.getStyleClass().add("attack-" + attack3.getType().toString());
        this.action3.setOnAction(e -> this.controller.playerAttack(attack3));
    }
}
