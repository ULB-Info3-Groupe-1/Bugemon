package ulb.views.combat.components;

import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Button;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.views.components.ComponentView;

/**
 * Action menu displaying the attacks available to the player's active Bugemon.
 *
 * <p>
 * Reads type-matchup efficiency directly from {@link CombatService} using the opponent {@link Trainer} reference set
 * via {@link #setOpponent(Trainer)}. Dispatches attack selections through the callback registered via
 * {@link #setOnAttack(Consumer)}.
 * </p>
 */
public class AttackMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AttackMenu.fxml";
    private static final double MIN_BUTTON_WIDTH = 200;

    private Trainer opponent;
    private Consumer<Attack> onAttack;
    private Runnable onBack;

    public AttackMenuView() {
        super(FXML_PATH);
    }

    /** Gives the menu the opponent trainer so it can compute type efficiency. */
    public void setOpponent(Trainer opponent) {
        this.opponent = opponent;
    }

    public void setOnAttack(Consumer<Attack> callback) {
        this.onAttack = callback;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }

    /** Clears and repopulates the menu with the given attacks. */
    public void show(List<Attack> attacks) {
        this.getChildren().clear();

        for (Attack attack : attacks) {
            this.getChildren().add(this.createAttackButton(attack));
        }

        Button back = new Button("Retour");
        back.getStyleClass().addAll("btn", "btn-secondary");
        back.setMinWidth(MIN_BUTTON_WIDTH);
        back.setOnAction(e -> {
            if (this.onBack != null) {
                this.onBack.run();
            }
        });
        this.getChildren().add(back);
    }

    private Button createAttackButton(Attack attack) {
        Efficiency efficiency = CombatService.compareBugemonType(attack.type(), this.opponent.getCurrentBugemonType());

        Button btn = new Button(attack.name() + "\n" + efficiency.toString());
        btn.getStyleClass().addAll("btn", "btn-secondary", "attack-" + attack.type().toString());
        btn.setMinWidth(MIN_BUTTON_WIDTH);
        btn.setOnAction(e -> {
            if (this.onAttack != null) {
                this.onAttack.accept(attack);
            }
        });
        return btn;
    }
}
