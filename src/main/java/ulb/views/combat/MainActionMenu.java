package ulb.views.combat;

import ulb.controllers.combat.ManualCombatController;

/**
 * The main action menu displayed during a manual combat turn.
 *
 * <p>
 * {@code MainActionMenu} is the root menu the player sees at the start of each
 * turn. It presents four choices:
 * <ol>
 *   <li><strong>Attaque</strong> — opens the {@link AttackActionMenu} so the
 *       player can pick a move.</li>
 *   <li><strong>Changer de Bugémon</strong> — opens the team panel so the
 *       player can voluntarily swap the active Bugémon (only available when
 *       {@link ManualCombatController#canSwitch()} returns {@code true}).</li>
 *   <li><strong>Utiliser un objet</strong> — reserved for future item use;
 *       currently has no action bound.</li>
 *   <li><strong>Abandonner</strong> — forfeits the match immediately via
 *       {@link ManualCombatController#surrender()}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Button actions are bound lazily: the menu is constructed without a controller
 * reference, and {@link #setController(ManualCombatController)} must be called
 * once before the player can interact with any button.
 * </p>
 *
 * @see AttackActionMenu
 * @see ManualCombatController
 * @see ulb.views.ActionMenuView
 */
public class MainActionMenu extends ActionMenuView {

    /**
     * Constructs a {@code MainActionMenu} and sets the display text and CSS
     * style classes for all four action buttons.
     *
     * <p>
     * No controller is wired at construction time; call
     * {@link #setController(ManualCombatController)} before displaying this
     * menu to the player.
     * </p>
     */
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
     * Binds each button to its corresponding controller callback.
     *
     * <ul>
     *   <li><em>Attaque</em> → {@link ManualCombatController#showAttackMenu()}</li>
     *   <li><em>Changer de Bugémon</em> →
     *       {@link ManualCombatController#showSwitchMenu()}, guarded by
     *       {@link ManualCombatController#canSwitch()} so the player cannot
     *       switch after already having switched this turn or while a forced
     *       post-KO switch is pending.</li>
     *   <li><em>Utiliser un objet</em> — no action bound yet (future feature).</li>
     *   <li><em>Abandonner</em> → {@link ManualCombatController#surrender()}</li>
     * </ul>
     *
     * @param controller the {@link ManualCombatController} to delegate actions
     *                   to; must not be {@code null}.
     */
    public void setController(ManualCombatController controller) {
        this.action1.setOnAction(e -> controller.showAttackMenu());

        this.action2.setOnAction(e -> {
            if (controller.canSwitch()) {
                controller.showSwitchMenu();
            }
        });

        this.action4.setOnAction(e -> controller.surrender());
    }
}
