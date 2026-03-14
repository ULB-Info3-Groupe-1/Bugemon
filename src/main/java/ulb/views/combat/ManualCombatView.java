package ulb.views.combat;

import java.io.IOException;
import java.util.List;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Attack;

/**
 * View for the manual combat screen, where the player actively selects actions
 * each turn.
 *
 * <p>
 * {@code ManualCombatView} extends {@link CombatView} by managing three
 * interchangeable action panels displayed in the {@code actionMenuView} slot:
 * <ul>
 *   <li>{@link MainActionMenu}  — the top-level menu (Attack / Switch / Item /
 *       Surrender).</li>
 *   <li>{@link AttackActionMenu} — the attack selection sub-menu, listing the
 *       active Bugemon's moves.</li>
 *   <li>The {@code bugemonTeamPane} — the team panel used both for voluntary
 *       switches and for forced post-KO switches.</li>
 * </ul>
 *
 * <p>
 * The view itself contains no game logic. Every user interaction (button click,
 * Bugemon selection) is forwarded to the {@link ManualCombatController} via the
 * callback set in {@link #setController(ManualCombatController)}.  Navigation
 * between panels is driven exclusively by the controller through the
 * {@code show*()} / {@code hide*()} methods defined here.
 * </p>
 *
 * @see CombatView
 * @see ManualCombatController
 * @see MainActionMenu
 * @see AttackActionMenu
 */
public class ManualCombatView extends CombatView {
    private ManualCombatController controller;

    private MainActionMenu mainActionMenu;
    private AttackActionMenu attackActionMenu;

    /**
     * Constructs a {@code ManualCombatView}, loads the shared
     * {@code Combat.fxml} layout, instantiates the two action-menu components,
     * and calls {@link #initCombatMode()} to display the initial UI state.
     *
     * @throws IOException if the {@code Combat.fxml} resource cannot be loaded.
     */
    public ManualCombatView() throws IOException {
        super();
        this.mainActionMenu = new MainActionMenu();
        this.attackActionMenu = new AttackActionMenu();
        this.initCombatMode();
    }

    /**
     * Initialises the combat UI for manual mode by displaying the main action
     * menu and ensuring the team panel is hidden.
     *
     * <p>
     * Called once during construction and again by
     * {@link ManualCombatController#runManualCombat} via
     * {@link #showScreenDebutCombat()} whenever a new combat session starts,
     * so that leftover state from a previous session is cleared.
     * </p>
     */
    @Override
    public void initCombatMode() {
        showMainActionMenu();
    }

    /**
     * Binds the given controller to this view and propagates it to both
     * action-menu components so their buttons can invoke controller callbacks.
     *
     * <p>
     * Must be called before the view is displayed for the first time.
     * </p>
     *
     * @param controller the {@link ManualCombatController} that handles all
     *                   user interactions; must not be {@code null}.
     */
    public void setController(ManualCombatController controller) {
        this.controller = controller;
        this.mainActionMenu.setController(this.controller);
        this.attackActionMenu.setController(this.controller);
    }

    /**
     * Replaces the content of the {@code actionMenuView} slot with the
     * {@link MainActionMenu}, giving the player access to the top-level
     * actions (Attack, Switch, Item, Surrender).
     */
    public void showMainActionMenu() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    /**
     * Replaces the content of the {@code actionMenuView} slot with the
     * {@link AttackActionMenu}, populated with the three attacks provided.
     *
     * <p>
     * The attack buttons are labelled with the attack name and its
     * type-matchup efficiency against the current opponent, as computed by
     * {@link ManualCombatController#isAttackEfficient}.
     * </p>
     *
     * @param attacks the list of {@link Attack}s available for the active
     *                Bugemon; must contain at least three elements (indices
     *                0, 1, and 2 are used).
     */
    public void showAttackMenu(List<Attack> attacks) {
        this.attackActionMenu.setAttacks(attacks.get(0), attacks.get(1), attacks.get(2));
        this.actionMenuView.getChildren().setAll(attackActionMenu);
    }

    /**
     * Makes the {@code bugemonTeamPane} visible and populates it with the
     * given list of Bugemons.
     *
     * <p>
     * Used for both voluntary switches (player chooses to swap during a turn)
     * and forced post-KO switches (active Bugemon just fainted). The
     * controller distinguishes the two cases via its {@code koSwitchFlag}; the
     * view simply forwards the selected Bugemon ID to
     * {@link ManualCombatController#switchBugemon(String)}.
     * </p>
     *
     * <p>
     * Navigation after the selection (hiding this panel, showing the main
     * menu) is handled entirely by the controller inside
     * {@link ManualCombatController#switchBugemon(String)}, not here.
     * </p>
     *
     * @param bugemonList the {@link BugemonDTO}s to display; should contain
     *                    only alive Bugemons so the player cannot select a
     *                    fainted one.
     */
    public void showSwitchMenu(List<BugemonDTO> bugemonList) {
        this.bugemonTeamPane.setVisible(true);
        this.bugemonTeamPane.setManaged(true);
        this.bugemonTeamView.setOnClickCallback(bugemon -> {
            if (bugemon != null) {
                this.controller.switchBugemon(bugemon.getId());
            }
        });
        this.bugemonTeamView.showTeam(bugemonList);
    }

    /**
     * Hides the {@code bugemonTeamPane} (the switch / team panel).
     */
    public void hideSwitchPanel() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    /**
     * Clears all children from the {@code actionMenuView} slot, effectively
     * hiding every action menu (main menu and attack menu).
     *
     * <p>
     * Called by the controller before opening the team panel so that the
     * two UI areas do not overlap.
     * </p>
     */
    public void hideAllActionMenus() {
        this.actionMenuView.getChildren().clear();
    }

    /**
     * Resets the view to its initial combat state: shows the main action menu
     * and hides the team panel.
     *
     * <p>
     * Called by {@link ManualCombatController#runManualCombat} at the start of
     * every new combat session.
     * </p>
     */
    public void showScreenDebutCombat() {
        showMainActionMenu();
        hideSwitchPanel();
    }
}
