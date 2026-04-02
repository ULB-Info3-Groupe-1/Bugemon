package ulb.views.combat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;

/**
 * View for the manual combat screen.
 *
 * <p>
 * Holds references to the {@link ManualTrainer}, the opponent {@link Trainer}, and {@link Combat} models. All sub-menu
 * navigation (attack menu, switch panel, main menu) is managed internally; the controller never calls any show/hide
 * method. User actions are dispatched through the callbacks registered via {@link #setOnAttack}, {@link #setOnSwitch},
 * and {@link #setOnSurrender}.
 * </p>
 */
public class ManualCombatView extends CombatView {
    private static final int BOX_DIM = 10;
    private ManualTrainer player;
    private Trainer opponent;
    private Combat combat;

    private final VBox itemPanel;

    private final MainActionMenuView mainActionMenu;
    private final AttackActionMenu attackActionMenu;
    private Consumer<Item> onItemSelected;
    private Consumer<Attack> onAttack;
    private Consumer<Bugemon> onSwitch;
    private Runnable onSurrender;

    public ManualCombatView() throws IOException {
        super();
        this.mainActionMenu = new MainActionMenuView();
        this.attackActionMenu = new AttackActionMenu();
        this.itemPanel = new VBox(BOX_DIM);
        this.itemPanel.setSpacing(BOX_DIM);
        this.initCombatMode();
    }

    /** Gives the view the model objects it reads from and wires the sub-menu callbacks. */
    public void setModel(ManualTrainer newPlayer, Trainer newOpponent, Combat newCombat) {
        this.player = newPlayer;
        this.opponent = newOpponent;
        this.combat = newCombat;

        this.mainActionMenu.setOnInventory(this::showInventory);
        this.mainActionMenu.setOnAttack(this::showAttackMenu);
        this.mainActionMenu.setOnSwitch(() -> this.showSwitchMenu(false));
        this.mainActionMenu.setOnSurrender(() -> {
            if (this.onSurrender != null) {
                this.onSurrender.run();
            }
        });

        this.attackActionMenu.setOpponent(this.opponent);
        this.attackActionMenu.setOnAttack(attack -> {
            if (this.onAttack != null) {
                this.onAttack.accept(attack);
            }
        });
        this.attackActionMenu.setOnBack(this::showMainActionMenu);
    }

    public void setOnAttack(Consumer<Attack> callback) {
        this.onAttack = callback;
    }

    public void setOnSwitch(Consumer<Bugemon> callback) {
        this.onSwitch = callback;
    }

    public void setOnSurrender(Runnable callback) {
        this.onSurrender = callback;
    }

    public void setOnItemSelected(Consumer<Item> callback) {
        this.onItemSelected = callback;
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

        updateTrainerBugemon(this.player.getCurrentBugemon());
        updateOpponentBugemon(this.opponent.getCurrentBugemon());

        TurnResult last = this.combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            showCombatDialog(last.first(), last.second());
        } else {
            hideDialog();
        }

        if (this.player.isForcedToSwitch()) {
            this.showSwitchMenu(true);
        } else {
            this.mainActionMenu.refresh(this.player.canVoluntarilySwitch());
            this.showMainActionMenu();
        }
    }

    // ── navigation ───────────────────────────────────────────────────

    public void showMainActionMenu() {
        this.actionMenuView.getChildren().setAll(this.mainActionMenu);
    }

    private void showAttackMenu() {
        List<Attack> attacks = this.player.getCurrentBugemonAttackList();
        this.attackActionMenu.setAttacks(attacks.get(0), attacks.get(1), attacks.get(2));
        this.actionMenuView.getChildren().setAll(this.attackActionMenu);
    }

    private void showSwitchMenu(boolean forced) {
        this.actionMenuView.getChildren().setAll(this.buildSwitchMenu(forced));
    }

    private void showInventory() {
        this.actionMenuView.getChildren().setAll(this.buildInventoryMenu());
    }

    private VBox buildSwitchMenu(boolean forced) {
        VBox panel = new VBox(BOX_DIM);
        panel.setAlignment(Pos.CENTER_RIGHT);

        List<Bugemon> available = this.player.getTeam().stream()
                .filter(b -> b != this.player.getCurrentBugemon() && b.isAlive()).toList();

        for (Bugemon b : available) {
            HBox row = new HBox(BOX_DIM);
            row.setAlignment(Pos.CENTER_LEFT);

            ImageView sprite = new ImageView(new Image(b.getSpriteURL(), 40, 40, true, false));
            sprite.setFitWidth(40);
            sprite.setFitHeight(40);
            sprite.setPreserveRatio(true);

            Button btn = new Button(
                    b.getName() + " Nv." + b.getLevel() + "  " + b.getHp() + "/" + b.getMaxHp() + " PV");
            btn.getStyleClass().addAll("btn", "btn-action-blue");
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);
            btn.setOnAction(e -> {
                if (this.onSwitch != null) {
                    this.onSwitch.accept(b);
                }
            });

            row.getChildren().addAll(sprite, btn);
            panel.getChildren().add(row);
        }

        if (!forced) {
            Button back = new Button("Retour");
            back.getStyleClass().addAll("btn", "btn-secondary");
            back.setMinWidth(200);
            back.setOnAction(e -> this.showMainActionMenu());
            panel.getChildren().add(back);
        }

        return panel;
    }

    private VBox buildInventoryMenu() {
        final VBox inventoryPanel = new VBox(BOX_DIM);
        inventoryPanel.setAlignment(Pos.CENTER_RIGHT);

        for (Map.Entry<Item, Integer> entry : this.player.getInventoryMap().entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();

            Button btn = new Button(item.name() + " ×" + quantity);

            btn.getStyleClass().add("switch-menu-button");
            btn.setMinWidth(200);
            btn.setOnAction(e -> {
                if (this.onItemSelected != null) {
                    this.onItemSelected.accept(item);
                }
            });

            inventoryPanel.getChildren().add(btn);
        }
        Button back = new Button("Retour");
        back.getStyleClass().add("action-button");
        back.setMinWidth(200);
        back.setOnAction(e -> this.showMainActionMenu());
        inventoryPanel.getChildren().add(back);

        return inventoryPanel;
    }
}
