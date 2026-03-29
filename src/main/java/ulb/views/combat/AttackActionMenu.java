package ulb.views.combat;

import java.util.function.Consumer;
import javafx.scene.control.Button;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;

/**
 * Action menu displaying the three attacks available to the player's active Bugemon.
 *
 * <p>
 * Reads type-matchup efficiency directly from {@link CombatService} using the
 * opponent {@link Trainer} reference set via {@link #setOpponent(Trainer)}.
 * Dispatches attack selections through the callback registered via
 * {@link #setOnAttack(Consumer)}. Holds no reference to any controller class.
 * </p>
 */
public class AttackActionMenu extends ActionMenuView {
    private Trainer opponent;
    private Consumer<Attack> onAttack;

    public AttackActionMenu() {
        super();
        this.action4.setText("Retour");
        this.action1.getStyleClass().add("attack");
        this.action2.getStyleClass().add("attack");
        this.action3.getStyleClass().add("attack");
    }

    /** Gives the menu the opponent trainer so it can compute type efficiency. */
    public void setOpponent(Trainer opponent) {
        this.opponent = opponent;
    }

    public void setOnAttack(Consumer<Attack> callback) {
        this.onAttack = callback;
    }

    public void setOnBack(Runnable callback) {
        this.action4.setOnAction(e -> {
            if (callback != null)
                callback.run();
        });
    }

    /** Populates the three attack buttons with the given moves and their type efficiency. */
    public void setAttacks(Attack attack1, Attack attack2, Attack attack3) {
        configureAttackButton(this.action1, attack1);
        configureAttackButton(this.action2, attack2);
        configureAttackButton(this.action3, attack3);
    }

    private void configureAttackButton(Button button, Attack attack) {
        button.getStyleClass().clear();
        button.getStyleClass().addAll("btn", "btn-secondary");

        Efficiency efficiency =
                CombatService.compareBugemonType(attack.type(), opponent.getCurrentBugemonType());

        button.setText(attack.name() + "\n" + efficiency.toString());
        button.getStyleClass().add("attack-" + attack.type().toString());
        button.setOnAction(e -> {
            if (onAttack != null)
                onAttack.accept(attack);
        });
    }
}
