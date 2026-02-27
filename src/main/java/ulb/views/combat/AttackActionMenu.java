package ulb.views.combat;

import ulb.views.ActionMenuView;

public class AttackActionMenu extends ActionMenuView {
    public AttackActionMenu() {
        super();
        this.action1.setText("Attack 1");
        this.action2.setText("Attack 2");
        this.action3.setText("Attack 3");
        this.action4.setText("Back");

        this.action1.getStyleClass().add("attack");
        this.action2.getStyleClass().add("attack");
        this.action3.getStyleClass().add("attack");
    }

    public void setAttacks(String attackName1, String attackName2, String attackName3) {
        // TODO: just give a list ...
    }
}
