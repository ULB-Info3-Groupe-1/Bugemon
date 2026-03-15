package ulb.fx_controllers.combat;

import ulb.controllers.combat.ManualCombatController;

public class MainActionMenu extends ActionMenuView {
    public MainActionMenu() {
        super();
        this.action1.setText("Attaque");
        this.action1.getStyleClass().add("action-attack");
        this.action2.setText("Changer de Bugémon");
        this.action2.getStyleClass().add("action-switch");
        this.action3.setText("Utiliser un objet");
        this.action3.getStyleClass().add("action-item");
        this.action4.setText("Abandonner");
    }

    /**
     * Set the controller for the main action menu and connect the buttons to the corresponding
     * actions in the controller
     * @param controller The ManualCombatController to set for the main action menu
     */
    public void setController(ManualCombatController controller) {
        this.action1.setOnAction(e -> controller.showAttackMenu());
        this.action2.setOnAction(e -> controller.showSwitchMenu());
        this.action4.setOnAction(e -> controller.surrender());
    }
}
