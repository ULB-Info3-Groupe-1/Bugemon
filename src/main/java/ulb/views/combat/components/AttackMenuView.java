package ulb.views.combat.components;

import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.views.components.ComponentView;

/**
 * Action menu displaying the attacks available to the player's active Bugemon. Presents up to three attack buttons in a
 * fixed 2×2 grid (mirroring {@link ActionMenuView}), plus a back button. Button labels and type-coloured styles are
 * applied via {@link #show(List, Trainer)} at display time.
 */
public class AttackMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AttackMenu.fxml";

    @FXML
    private Button topLeft;
    @FXML
    private Button topRight;
    @FXML
    private Button bottomLeft;

    private final Attack[] attacks = new Attack[3];
    private Consumer<Attack> onAttack;
    private Runnable onBack;

    public AttackMenuView() {
        super(FXML_PATH);
    }

    public void setOnAttack(Consumer<Attack> callback) {
        this.onAttack = callback;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }

    /**
     * Populates the three attack slots with the given attacks, computing type-matchup efficiency against the opponent.
     *
     * @param attackList
     *            attacks available to the active Bugemon (up to 3)
     * @param opponent
     *            the opposing trainer, used to derive type-matchup labels
     */
    public void show(List<Attack> attackList, Trainer opponent) {
        Button[] buttons = {this.topLeft, this.topRight, this.bottomLeft};
        for (int i = 0; i < buttons.length; i++) {
            if (i < attackList.size()) {
                Attack attack = attackList.get(i);
                this.attacks[i] = attack;
                Efficiency eff = CombatService.compareBugemonType(attack.type(), opponent.getCurrentBugemonType());
                buttons[i].setText(attack.name() + "\n" + eff.toString());
                buttons[i].getStyleClass().setAll("btn", "attack-" + attack.type().toString());
                buttons[i].setVisible(true);
                buttons[i].setManaged(true);
            } else {
                this.attacks[i] = null;
                buttons[i].setVisible(false);
                buttons[i].setManaged(false);
            }
        }
    }

    @FXML
    private void onAttack1Clicked() {
        if (this.attacks[0] != null && this.onAttack != null) {
            this.onAttack.accept(this.attacks[0]);
        }
    }

    @FXML
    private void onAttack2Clicked() {
        if (this.attacks[1] != null && this.onAttack != null) {
            this.onAttack.accept(this.attacks[1]);
        }
    }

    @FXML
    private void onAttack3Clicked() {
        if (this.attacks[2] != null && this.onAttack != null) {
            this.onAttack.accept(this.attacks[2]);
        }
    }

    @FXML
    private void onBackClicked() {
        if (this.onBack != null) {
            this.onBack.run();
        }
    }
}
