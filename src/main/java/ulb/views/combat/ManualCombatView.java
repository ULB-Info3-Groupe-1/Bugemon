package ulb.views.combat;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.views.combat.components.ActionMenuView;
import ulb.views.combat.components.AttackMenuView;
import ulb.views.combat.components.ItemMenuView;
import ulb.views.combat.components.SwitchMenuView;

/**
 * View for the manual combat screen.
 *
 * <p>
 * Holds references to the {@link ManualTrainer}, the opponent {@link Trainer}, and {@link Combat} models. All sub-menu
 * navigation (attack menu, switch panel, inventory) is managed internally; the controller never calls any show/hide
 * method. User actions are dispatched through the {@link Listener} registered via {@link #setListener(Listener)}.
 * </p>
 */
public class ManualCombatView extends CombatView {
    private ManualTrainer player;
    private Trainer opponent;
    private Combat combat;

    private final ActionMenuView actionMenu;
    private final AttackMenuView attackMenu;
    private final SwitchMenuView switchMenu;
    private final ItemMenuView itemMenuView;

    private Listener listener;

    public ManualCombatView() {
        super();
        this.actionMenu = new ActionMenuView();
        this.attackMenu = new AttackMenuView();
        this.switchMenu = new SwitchMenuView();
        this.itemMenuView = new ItemMenuView();
    }

    /** Registers the listener that receives all user combat action events. */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Gives the view the model objects it reads from in {@link #refresh()}. */
    public void setModel(ManualTrainer newPlayer, Trainer newOpponent, Combat newCombat) {
        this.player = newPlayer;
        this.opponent = newOpponent;
        this.combat = newCombat;
        this.attackMenu.setOpponent(this.opponent);
    }

    @Override
    protected void initCombatMode() {
        this.initMenuCallbacks();
        this.showMainActionMenu();
    }

    @Override
    public void refresh() {
        if (this.player == null) {
            return;
        }

        this.updateTrainerBugemon(this.player.getCurrentBugemon());
        this.updateOpponentBugemon(this.opponent.getCurrentBugemon());

        TurnResult last = this.combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            this.showCombatDialog(last.first(), last.second());
        } else {
            this.hideDialog();
        }

        if (this.player.isForcedToSwitch()) {
            this.showSwitchMenu(true);
        } else {
            this.actionMenu.refresh(this.player.canVoluntarilySwitch());
            this.showMainActionMenu();
        }
    }

    // ── Sub-menu navigation ───────────────────────────────────────────────────

    private void initMenuCallbacks() {
        this.actionMenu.setOnAttack(this::showAttackMenu);
        this.actionMenu.setOnSwitch(() -> this.showSwitchMenu(false));
        this.actionMenu.setOnInventory(this::showInventory);
        this.actionMenu.setOnSurrender(() -> {
            if (this.listener != null) {
                this.listener.onSurrender();
            }
        });

        this.attackMenu.setOnBack(this::showMainActionMenu);
        this.attackMenu.setOnAttack(attack -> {
            if (this.listener != null) {
                this.listener.onAttack(attack);
            }
        });

        this.switchMenu.setOnBack(this::showMainActionMenu);
        this.switchMenu.setOnSwitch(bugemon -> {
            if (this.listener != null) {
                this.listener.onSwitch(bugemon);
            }
        });

        this.itemMenuView.setOnBack(this::showMainActionMenu);
        this.itemMenuView.setOnItemSelected(item -> {
            if (this.listener != null) {
                this.listener.onItemSelected(item);
            }
        });
    }

    public void showMainActionMenu() {
        this.setActionMenuContent(this.actionMenu);
    }

    private void showAttackMenu() {
        List<Attack> attacks = this.player.getCurrentBugemonAttackList();
        this.attackMenu.show(attacks);
        this.setActionMenuContent(this.attackMenu);
    }

    private void showSwitchMenu(boolean forced) {
        List<Bugemon> available = this.player.getTeam().stream()
                .filter(b -> b != this.player.getCurrentBugemon() && b.isAlive()).toList();
        this.switchMenu.show(available, forced);
        this.setActionMenuContent(this.switchMenu);
    }

    private void showInventory() {
        this.itemMenuView.show(this.player.getInventoryMap());
        this.setActionMenuContent(this.itemMenuView);
    }

    /** Callback interface for all user combat actions dispatched by this view. */
    public interface Listener {
        void onAttack(Attack attack);

        void onSwitch(Bugemon bugemon);

        void onSurrender();

        void onItemSelected(Item item);
    }
}
