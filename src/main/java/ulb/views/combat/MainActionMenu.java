package ulb.views.combat;

import ulb.views.ActionMenuView;

public class MainActionMenu extends ActionMenuView {
    public MainActionMenu() {
        super();
        this.action1.setText("Attack");
        this.action1.getStyleClass().add("action-attack");
        this.action2.setText("Switch");
        this.action2.getStyleClass().add("action-switch");
        this.action3.setText("Use Item");
        this.action3.getStyleClass().add("action-item");
        this.action4.setText("Surrender");
    }
}
