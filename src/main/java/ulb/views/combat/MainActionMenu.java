package ulb.views.combat;

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
     * Set the action to perform when the attack button is clicked.
     * @param action the action to perform
     */
    public void setOnAttack(Runnable action) {
        this.action1.setOnAction(e -> action.run());
    }

    /**
     * Set the action to perform when the switch button is clicked.
     * @param action the action to perform
     */
    public void setOnSwitch(Runnable action) {
        this.action2.setOnAction(e -> action.run());
    }

    /**
     * Set the action to perform when the surrender button is clicked.
     * @param action the action to perform
     */
    public void setOnSurrender(Runnable action) {
        this.action4.setOnAction(e -> action.run());
    }
}
