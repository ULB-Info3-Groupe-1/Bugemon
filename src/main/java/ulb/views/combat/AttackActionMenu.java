package ulb.views.combat;

import java.util.function.Consumer;

import javafx.scene.control.Button;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.services.CombatService;

public class AttackActionMenu extends ActionMenuView {
    private Consumer<Attack> onAttackSelected;

    public AttackActionMenu() {
        super();
        this.action4.setText("Retour");

        this.action1.getStyleClass().add("attack");
        this.action2.getStyleClass().add("attack");
        this.action3.getStyleClass().add("attack");
    }

    /**
     * Set the action to perform when the back button is clicked.
     * @param action the action to perform
     */
    public void setOnBack(Runnable action) {
        this.action4.setOnAction(e -> action.run());
    }

    /**
     * Set the callback to be invoked when an attack is selected.
     * @param onAttackSelected the Consumer that will handle the selected Attack
     */
    public void setOnAttackSelected(Consumer<Attack> onAttackSelected) {
        this.onAttackSelected = onAttackSelected;
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

        if (this.onAttackSelected == null) {
            button.setOnAction(null);
            return;
        }
        button.setOnAction(e -> this.onAttackSelected.accept(attack));
    }
}
