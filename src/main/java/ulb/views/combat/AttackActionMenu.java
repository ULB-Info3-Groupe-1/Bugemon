package ulb.views.combat;

import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;
/**
 * Action menu component displaying the three attacks available to the player's
 * current {@link ulb.models.bugemon.Bugemon} during a manual combat turn.
 *
 * <p>
 * {@code AttackActionMenu} extends {@link ActionMenuView} and occupies the
 * {@code action1}–{@code action3} buttons with the Bugemon's move-set, each
 * labelled with the attack name and its type-matchup efficiency against the
 * current opponent. The fourth button ({@code action4}) is always a "Back"
 * button that returns the player to the {@link MainActionMenu}.
 * </p>
 *
 * <p>
 * The menu is stateless between turns: {@link #setAttacks(Attack, Attack, Attack)}
 * must be called every time the player opens the attack menu so that the buttons
 * reflect the active Bugemon's current move-set and the opponent's current type.
 * </p>
 *
 * <p>
 * A {@link ManualCombatController} must be injected via
 * {@link #setController(ManualCombatController)} before the menu is displayed;
 * failing to do so will result in a {@link NullPointerException} when any button
 * is clicked.
 * </p>
 *
 * @see MainActionMenu
 * @see ManualCombatController
 * @see ActionMenuView
 */
public class AttackActionMenu extends ActionMenuView {
    /** The controller used to forward player actions to the combat model. */
    private ManualCombatController controller;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs an {@code AttackActionMenu} with default labels.
     *
     * <p>
     * The three attack buttons ({@code action1}–{@code action3}) receive the
     * {@code "attack"} CSS class as a base style; their text and on-action
     * handlers are set later by {@link #setAttacks(Attack, Attack, Attack)}.
     * The fourth button is immediately labelled "Retour".
     * </p>
     *
     * <p>
     * Note: {@link #setController(ManualCombatController)} must be called before
     * any button interaction occurs.
     * </p>
     */
    public AttackActionMenu() {
        super();
        this.action4.setText("Retour");

        this.action1.getStyleClass().add("attack");
        this.action2.getStyleClass().add("attack");
        this.action3.getStyleClass().add("attack");
    }

    // ── Controller binding ────────────────────────────────────────────────────

    /**
     * Binds a {@link ManualCombatController} to this menu and wires the "Back"
     * button to {@link ManualCombatController#showMainActionMenu()}.
     *
     * <p>
     * This method must be called once before the menu is shown to the player.
     * The attack buttons' handlers are set by
     * {@link #setAttacks(Attack, Attack, Attack)} and therefore also depend on
     * this controller being non-null.
     * </p>
     *
     * @param controller the {@link ManualCombatController} to forward player
     *                   actions to; must not be {@code null}.
     */
    public void setController(ManualCombatController controller) {
        this.controller = controller;
        this.action4.setOnAction(e -> controller.showMainActionMenu());
    }

    // ── Attack population ─────────────────────────────────────────────────────

    /**
     * Populates the three attack buttons with the given moves.
     *
     * <p>
     * Each button is labelled with the attack's name and its type-matchup
     * efficiency against the opponent's current Bugemon type (obtained via
     * {@link ManualCombatController#getOpponentBugemonType()}). A CSS class
     * {@code "attack-<TYPE>"} is also applied so that each button can be styled
     * by elemental type.
     * </p>
     *
     * <p>
     * Clicking a button calls
     * {@link ManualCombatController#playerAttack(Attack)} with the corresponding
     * attack, which queues the action on the {@link ulb.models.trainer.ManualTrainer}
     * and triggers {@link ulb.models.combat.Combat#turn()}.
     * </p>
     *
     * <p>
     * This method clears all previous style classes on each button before
     * applying the new ones, so it is safe to call multiple times per combat.
     * </p>
     *
     * @param attack1 the first {@link Attack} to display; must not be
     *                {@code null}.
     * @param attack2 the second {@link Attack} to display; must not be
     *                {@code null}.
     * @param attack3 the third {@link Attack} to display; must not be
     *                {@code null}.
     */
    public void setAttacks(Attack attack1, Attack attack2, Attack attack3) {
        configureAttackButton(this.action1, attack1);
        configureAttackButton(this.action2, attack2);
        configureAttackButton(this.action3, attack3);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Configures a single attack button: clears its style classes, sets its
     * label, applies the elemental type CSS class, and wires its on-action
     * handler.
     *
     * @param button the {@link javafx.scene.control.Button} to configure; must
     *               not be {@code null}.
     * @param attack the {@link Attack} to bind to this button; must not be
     *               {@code null}.
     */
    private void configureAttackButton(javafx.scene.control.Button button, Attack attack) {
        button.getStyleClass().clear();
        button.getStyleClass().add("action-button");

        String efficiency = this.controller.isAttackEfficient(attack).toString();

        button.setText(attack.getName() + "\n" + efficiency);
        button.getStyleClass().add("attack-" + attack.getType().toString());
        button.setOnAction(e -> this.controller.playerAttack(attack));
    }
}
