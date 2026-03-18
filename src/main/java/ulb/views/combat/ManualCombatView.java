package ulb.views.combat;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;

/**
 * View for the manual combat screen.
 *
 * <p>
 * Holds references to the {@link ManualTrainer}, {@link AutoTrainer}, and
 * {@link Combat} models. All sub-menu navigation (attack menu, switch panel,
 * main menu) is managed internally; the controller never calls any show/hide
 * method. User actions are dispatched through the callbacks registered via
 * {@link #setOnAttack}, {@link #setOnSwitch}, and {@link #setOnSurrender}.
 * </p>
 */
public class ManualCombatView extends CombatView {
    private ManualTrainer player;
    private AutoTrainer opponent;
    private Combat combat;

    private final MainActionMenu mainActionMenu;
    private final AttackActionMenu attackActionMenu;

    private Consumer<Attack> onAttack;
    private Consumer<Bugemon> onSwitch;
    private Runnable onSurrender;

    public ManualCombatView() throws IOException {
        super();
        this.mainActionMenu = new MainActionMenu();
        this.attackActionMenu = new AttackActionMenu();
        this.initCombatMode();
    }

    /**
     * Gives the view the model objects it reads from and wires the sub-menu
     * callbacks.
     *
     * @param player      the player's trainer model; must not be {@code null}.
     * @param opponent    the opponent's trainer model; must not be {@code null}.
     * @param combat      the combat model; must not be {@code null}.
     * @param onAttack    callback to invoke when the user confirms an attack
     *                    choice; must not be {@code null}.
     * @param onSwitch    callback to invoke when the user confirms a switch choice;
     *                    must not be {@code null}.
     * @param onSurrender callback to invoke when the user confirms a surrender
     *                    action; must not be {@code null}.
     * 
     */
    public void setModel(ManualTrainer player, AutoTrainer opponent, Combat combat) {
        this.player = player;
        this.opponent = opponent;
        this.combat = combat;

        this.mainActionMenu.setOnAttack(this::showAttackMenu);
        this.mainActionMenu.setOnSwitch(() -> showSwitchMenu(false));
        this.mainActionMenu.setOnSurrender(() -> {
            if (onSurrender != null)
                onSurrender.run();
        });

        this.attackActionMenu.setOpponent(opponent);
        this.attackActionMenu.setOnAttack(attack -> {
            if (onAttack != null)
                onAttack.accept(attack);
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

    @Override
    protected void initCombatMode() {
        showMainActionMenu();
    }

    @Override
    public void refresh() {
        if (player == null)
            return;

        refreshCombatTurn(combat, player, opponent);

        if (player.isForcedToSwitch()) {
            showSwitchMenu(true);
        } else {
            mainActionMenu.refresh(player.canVoluntarilySwitch());
            showMainActionMenu();
        }
    }

    // ── navigation ───────────────────────────────────────────────────

    private void showMainActionMenu() {
        this.actionMenuView.getChildren().setAll(mainActionMenu);
    }

    private void showAttackMenu() {
        List<Attack> attacks = player.getCurrentBugemonAttackList();
        this.attackActionMenu.setAttacks(attacks.get(0), attacks.get(1), attacks.get(2));
        this.actionMenuView.getChildren().setAll(attackActionMenu);
    }

    private void showSwitchMenu(boolean forced) {
        this.actionMenuView.getChildren().setAll(buildSwitchMenu(forced));
    }

    private VBox buildSwitchMenu(boolean forced) {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER_RIGHT);

        List<Bugemon> available = player.getTeam()
                .stream()
                .filter(b -> b != player.getCurrentBugemon() && b.isAlive())
                .collect(Collectors.toList());

        for (Bugemon b : available) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            ImageView sprite = new ImageView(new Image(b.getSpriteURL()));
            sprite.setFitWidth(40);
            sprite.setFitHeight(40);
            sprite.setPreserveRatio(true);

            Button btn = new Button(b.getName() + " Nv." + b.getLevel() + "  " + b.getHp() + "/"
                    + b.getMaxHp() + " PV");
            btn.getStyleClass().add("switch-menu-button");
            btn.setMinWidth(200);
            btn.setOnAction(e -> {
                if (onSwitch != null)
                    onSwitch.accept(b);
            });

            row.getChildren().addAll(sprite, btn);
            panel.getChildren().add(row);
        }

        if (!forced) {
            Button back = new Button("Retour");
            back.getStyleClass().add("action-button");
            back.setMinWidth(200);
            back.setOnAction(e -> showMainActionMenu());
            panel.getChildren().add(back);
        }

        return panel;
    }
}
