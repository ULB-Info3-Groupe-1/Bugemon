package ulb.views.combat;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.views.combat.components.ActionMenuView;
import ulb.views.combat.components.AttackMenuView;
import ulb.views.combat.components.ItemMenuView;
import ulb.views.combat.components.SwitchMenuView;

/**
 * View for the manual combat screen. All sub-menu navigation (attack, switch, inventory) is managed internally; the
 * controller only calls {@link #setModel(ManualTrainer, Trainer)} and {@link #setListener(Listener)}.
 */
public class ManualCombatView extends CombatView {
    private ManualTrainer player;
    private Trainer opponent;

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

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Gives the view the model objects it reads from in {@link #refresh()}. */
    public void setModel(ManualTrainer newPlayer, Trainer newOpponent) {
        this.player = newPlayer;
        this.opponent = newOpponent;
    }

    @Override
    protected void initCombatMode() {
        this.initMenuCallbacks();
        this.setDialogNextCallback(() -> {
            if (this.listener != null) {
                this.listener.onNext();
            }
        });
        this.showMainActionMenu();
    }

    @Override
    public void refresh() {
        if (this.player == null) {
            return;
        }

        this.updateTrainerBugemon(this.player.getCurrentBugemon());
        this.updateOpponentBugemon(this.opponent.getCurrentBugemon());

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

    /** Restores the main action menu, called after a forced switch completes. */
    public void showMainActionMenu() {
        this.setActionMenuContent(this.actionMenu);
    }

    private void showAttackMenu() {
        List<Attack> attacks = this.player.getCurrentBugemonAttackList();
        this.attackMenu.show(attacks, this.opponent);
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

        void onNext();
    }
}
