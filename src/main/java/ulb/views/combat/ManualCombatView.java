package ulb.views.combat;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.Item;
import ulb.models.combat.TurnStep;
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

    private void initActionMenuViewListener() {
        this.actionMenu.setListener(new ActionMenuView.Listener() {

            @Override
            public void onAttack() {
                ManualCombatView.this.showAttackMenu();
            }

            @Override
            public void onSwitch() {
                ManualCombatView.this.showSwitchMenu(false);
            }

            @Override
            public void onInventory() {
                ManualCombatView.this.showInventory();
            }

            @Override
            public void onSurrender() {
                ManualCombatView.this.listener.onSurrender();
            }

        });
    }

    private void initAttackMenuListener() {
        this.attackMenu.setListener(new AttackMenuView.Listener() {

            @Override
            public void onAttack(Attack attack) {
                ManualCombatView.this.listener.onAttack(attack);
            }

            @Override
            public void onAttackHovered(Attack attack) {
                Efficiency eff = attack.getEfficiencyAgainst(ManualCombatView.this.opponent.getCurrentBugemon());
                ManualCombatView.this.showHoverInfo(attack.name(), "Type : " + attack.type(),
                        "Puissance : " + attack.power(), attack.description().isBlank() ? null : attack.description());
                ManualCombatView.this.setHoverType(attack.type());
                ManualCombatView.this.setHoverEfficiency(eff);
            }

            @Override
            public void onAttackLeft() {
                ManualCombatView.this.hideHoverInfo();
            }

            @Override
            public void onBack() {
                ManualCombatView.this.hideHoverInfo();
                ManualCombatView.this.showMainActionMenu();
            }

        });
    }

    private void initSwitchMenuListener() {
        this.switchMenu.setListener(new SwitchMenuView.Listener() {

            @Override
            public void onSwitch(Bugemon bugemon) {
                ManualCombatView.this.listener.onSwitch(bugemon);
            }

            @Override
            public void onBack() {
                ManualCombatView.this.showMainActionMenu();
            }

        });
    }

    private void initItemMenuListener() {
        this.itemMenuView.setListener(new ItemMenuView.Listener() {

            @Override
            public void onItemSelected(Item item) {
                ManualCombatView.this.listener.onItemSelected(item);
            }

            @Override
            public void onItemHovered(Item item) {
                ManualCombatView.this.showHoverInfo(item.name(), "Catégorie : " + item.type(),
                        item.description().isBlank() ? null : item.description());
            }

            @Override
            public void onItemLeft() {
                ManualCombatView.this.hideHoverInfo();
            }

            @Override
            public void onBack() {
                ManualCombatView.this.hideHoverInfo();
                ManualCombatView.this.showMainActionMenu();
            }

        });
    }

    private void initListeners() {
        this.initActionMenuViewListener();
        this.initAttackMenuListener();
        this.initSwitchMenuListener();
        this.initItemMenuListener();
    }

    public ManualCombatView() {
        super();

        this.actionMenu = new ActionMenuView();
        this.attackMenu = new AttackMenuView();
        this.switchMenu = new SwitchMenuView();
        this.itemMenuView = new ItemMenuView();

        this.initListeners();
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

    @Override
    public void showStepDialog(TurnStep step, Trainer playerTrainer) {
        this.hideHoverInfo();
        this.hideActionMenu();
        super.showStepDialog(step, playerTrainer);
    }

    @Override
    public void hideDialog() {
        super.hideDialog();
        this.showActionMenu();
    }

    /** Callback interface for all player combat actions dispatched by this view. */
    public interface Listener {
        void onAttack(Attack attack);

        void onSwitch(Bugemon bugemon);

        void onSurrender();

        void onItemSelected(Item item);
    }
}
