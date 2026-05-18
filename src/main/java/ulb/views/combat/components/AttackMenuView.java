package ulb.views.combat.components;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.views.components.ComponentView;

/**
 * Action menu displaying the attacks available to the player's active Bugemon. Presents up to three attack buttons in a
 * fixed 2×2 grid (mirroring {@link ActionMenuView}), plus a back button. Button labels and type-coloured styles are
 * applied via {@link #show(List, Trainer)} at display time.
 */
public class AttackMenuView extends ComponentView {

    @FXML
    private Button topLeftButton;
    @FXML
    private Button topRightButton;
    @FXML
    private Button bottomLeftButton;

    private final Attack[] attacks = new Attack[3];
    private Listener listener;

    public AttackMenuView() {
        super(Configuration.Paths.Fxml.COMPONENT_ATTACK_MENU);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Populates the three attack slots with the given attacks, computing type-matchup efficiency against the opponent.
     *
     * @param attackList
     *            attacks available to the active Bugemon (up to 3)
     */
    public void show(List<Attack> attackList) {
        Button[] buttons = {this.topLeftButton, this.topRightButton, this.bottomLeftButton};
        for (int i = 0; i < buttons.length; i++) {
            if (i < attackList.size()) {
                Attack attack = attackList.get(i);
                this.attacks[i] = attack;
                buttons[i].setText(attack.name());
                buttons[i].getStyleClass().setAll("btn", "attack-" + attack.type().toString());
                buttons[i].setVisible(true);
                buttons[i].setManaged(true);
                buttons[i].setOnMouseEntered(e -> this.listener.onAttackHovered(attack));
                buttons[i].setOnMouseExited(e -> this.listener.onAttackUnhovered());
            } else {
                this.attacks[i] = null;
                buttons[i].setVisible(false);
                buttons[i].setManaged(false);
            }
        }
    }

    @FXML
    private void onAttack1Clicked() {
        if (this.attacks[0] != null) {
            this.listener.onAttackChosen(this.attacks[0]);
        }

    }

    @FXML
    private void onAttack2Clicked() {
        if (this.attacks[1] != null) {
            this.listener.onAttackChosen(this.attacks[1]);
        }
    }

    @FXML
    private void onAttack3Clicked() {
        if (this.attacks[2] != null) {
            this.listener.onAttackChosen(this.attacks[2]);
        }
    }

    @FXML
    private void onBackClicked() {
        this.listener.onBack();
    }

    public interface Listener {

        void onAttackChosen(Attack attack);

        void onAttackHovered(Attack attack);

        void onAttackUnhovered();

        void onBack();
    }
}
