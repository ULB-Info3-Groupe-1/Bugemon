package ulb.views.combat;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;

/**
 * View for the manual combat screen.
 *
 * <p>
 * Holds references to the {@link ManualTrainer}, {@link AutoTrainer}, and
 * {@link Combat} models and reads their state in {@link #refresh()}. All
 * sub-menu navigation (attack menu, switch panel, main menu) is managed
 * internally by this view; the controller never calls any show/hide method.
 * </p>
 *
 * <p>
 * User actions are dispatched through the callbacks registered via
 * {@link #setOnAttack}, {@link #setOnSwitch}, and {@link #setOnSurrender}.
 * No concrete controller reference is held.
 * </p>
 */
public class ManualCombatView extends CombatView {
    private ManualTrainer player;
    private AutoTrainer opponent;
    private Combat combat;

    private final MainActionMenu mainActionMenu;
    private final AttackActionMenu attackActionMenu;

    private Consumer<Attack> onAttack;
    private Consumer<String> onSwitch;
    private Runnable onSurrender;

    public ManualCombatView() throws IOException {
        super();
        this.mainActionMenu = new MainActionMenu();
        this.attackActionMenu = new AttackActionMenu();
        this.initCombatMode();
    }

    /**
     * Gives the view the model objects it reads from, and wires the internal
     * sub-menu navigation callbacks.
     */
    public void setModel(ManualTrainer player, AutoTrainer opponent, Combat combat) {
        this.player = player;
        this.opponent = opponent;
        this.combat = combat;

        this.mainActionMenu.setOnAttack(this::showAttackMenuInternal);
        this.mainActionMenu.setOnSwitch(this::showSwitchPanelInternal);
        this.mainActionMenu.setOnSurrender(() -> { if (onSurrender != null) onSurrender.run(); });

        this.attackActionMenu.setOpponent(opponent);
        this.attackActionMenu.setOnAttack(
                attack -> { if (onAttack != null) onAttack.accept(attack); });
        this.attackActionMenu.setOnBack(this::showMainActionMenuInternal);

        this.bugemonTeamView.setOnClickCallback(dto -> {
            if (dto != null && onSwitch != null) onSwitch.accept(dto.getId());
        });
    }

    public void setOnAttack(Consumer<Attack> callback) {
        this.onAttack = callback;
    }

    public void setOnSwitch(Consumer<String> callback) {
        this.onSwitch = callback;
    }

    public void setOnSurrender(Runnable callback) {
        this.onSurrender = callback;
    }

    @Override
    protected void initCombatMode() {
        showMainActionMenuInternal();
        hideSwitchPanelInternal();
    }

    @Override
    public void refresh() {
        if (player == null) return;

        updateTrainerBugemon(player.getCurrentBugemon());
        updateOpponentBugemon(opponent.getCurrentBugemon());

        TurnResult last = combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            showCombatDialog(last.first(), last.second());
        } else {
            hideDialog();
        }

        if (player.isForcedToSwitch()) {
            hideAllActionMenusInternal();
            showSwitchPanelInternal();
        } else {
            hideSwitchPanelInternal();
            mainActionMenu.refresh(player.canVoluntarilySwitch());
            showMainActionMenuInternal();
        }
    }

    // ── Internal navigation (never called by the controller) ─────────────────

    private void showMainActionMenuInternal() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    private void showAttackMenuInternal() {
        List<Attack> attacks = player.getCurrentBugemonAttackList();
        this.attackActionMenu.setAttacks(attacks.get(0), attacks.get(1), attacks.get(2));
        this.actionMenuView.getChildren().setAll(attackActionMenu);
    }

    private void showSwitchPanelInternal() {
        List<BugemonDTO> alive = player.getTeam().stream()
                .filter(Bugemon::isAlive)
                .map(b -> (BugemonDTO) b)
                .toList();
        this.bugemonTeamPane.setVisible(true);
        this.bugemonTeamPane.setManaged(true);
        this.bugemonTeamView.showTeam(alive);
    }

    private void hideSwitchPanelInternal() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    private void hideAllActionMenusInternal() {
        this.actionMenuView.getChildren().clear();
    }
}
