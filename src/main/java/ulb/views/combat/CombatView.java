package ulb.views.combat;

import java.io.File;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.Efficiency;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.HealBugemonStep;
import ulb.models.combat.turn.TurnStep.HealTeamStep;
import ulb.models.combat.turn.TurnStep.ItemStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.models.item.Item;
import ulb.views.View;
import ulb.views.combat.components.ActionMenuView;
import ulb.views.combat.components.AttackMenuView;
import ulb.views.combat.components.BugemonInfoView;
import ulb.views.combat.components.ItemMenuView;
import ulb.views.combat.components.SwitchMenuView;
import ulb.views.components.DialogZoneView;
import ulb.views.components.HoverInfoView;

/**
 * Root view for the combat screen. Manages two {@link BugemonInfoView} HUD panels (player and opponent), both Bugemon
 * sprite {@link ImageView}s, an action-menu slot that swaps between {@link ActionMenuView}, {@link AttackMenuView},
 * {@link SwitchMenuView}, and {@link ItemMenuView}, a {@link HoverInfoView} for attack or item previews, and a
 * {@link DialogZoneView} for turn narration.
 *
 * <p>
 * User actions flow out through {@link Listener}; turn-step acknowledgements flow out through {@link NextListener}.
 * Animations (lunge-and-recoil on attack, fade-out on KO) automatically disable the Next button for their duration.
 */
public class CombatView extends View {

    @FXML
    private BugemonInfoView bugemonPlayerInfo;
    @FXML
    private BugemonInfoView bugemonOpponentInfo;
    @FXML
    private ImageView bugemonPlayerImage;
    @FXML
    private ImageView bugemonOpponentImage;
    @FXML
    private VBox actionMenuSlot;
    @FXML
    private HoverInfoView hoverInfoView;
    @FXML
    private DialogZoneView dialogZoneView;

    private final ActionMenuView actionMenu;
    private final AttackMenuView attackMenu;
    private final SwitchMenuView switchMenu;
    private final ItemMenuView itemMenuView;

    private CombatBugemon playerBugemon;
    private CombatBugemon opponentBugemon;

    private Listener listener;
    private NextListener nextListener;

    public CombatView() {
        super();
        this.actionMenu = new ActionMenuView();
        this.attackMenu = new AttackMenuView();
        this.switchMenu = new SwitchMenuView();
        this.itemMenuView = new ItemMenuView();

        this.initListeners();
    }

    @FXML
    protected void initialize() {
        this.dialogZoneView.setListener(() -> {
            if (this.nextListener != null) {
                this.nextListener.onNext();
            }
        });
        this.showMainActionMenu();
    }

    /**
     * Displays both Bugemons in their initial state — loads sprites and HUD data, then hides the dialog zone.
     *
     * @param newPlayerBugemon
     *            the player's active Bugemon
     * @param newOpponentBugemon
     *            the opponent's active Bugemon
     */
    public void displayBugemons(CombatBugemon newPlayerBugemon, CombatBugemon newOpponentBugemon) {
        this.playerBugemon = newPlayerBugemon;
        this.opponentBugemon = newOpponentBugemon;
        this.refreshPlayer();
        this.refreshOpponent();

        this.hideDialog();
    }

    /**
     * Updates the HUD for the Bugemon that just switched in, then synchronises its HP bar to the HP it had at the
     * moment of the switch.
     *
     * @param switchStep
     *            the switch step describing which Bugemon entered and its HP at that point
     */
    public void switchBugemon(TurnStep.SwitchStep switchStep) {
        if (switchStep.isPlayer()) {
            this.playerBugemon = switchStep.bugemon();
            this.refreshPlayer();
        } else {
            this.opponentBugemon = switchStep.bugemon();
            this.refreshOpponent();
        }

        this.updateHp(switchStep.bugemon(), switchStep.hpAtSwitch());
    }

    /**
     * Updates the HP bar for the given Bugemon.
     *
     * @param bugemon
     *            the Bugemon whose HP changed
     * @param currentHp
     *            the new current HP value
     */
    public void updateHp(CombatBugemon bugemon, int currentHp) {
        if (bugemon == this.playerBugemon) {
            this.bugemonPlayerInfo.setHp(currentHp, bugemon.getMaxHp());
        } else if (bugemon == this.opponentBugemon) {
            this.bugemonOpponentInfo.setHp(currentHp, bugemon.getMaxHp());
        }
    }

    private void refreshPlayer() {
        this.bugemonPlayerInfo.setBugemonInfo(this.playerBugemon);
        this.setSprite(this.bugemonPlayerImage, this.playerBugemon.getSpritePath());
        this.bugemonPlayerImage.setOpacity(1.0);
    }

    private void refreshOpponent() {
        this.bugemonOpponentInfo.setBugemonInfo(this.opponentBugemon);
        this.setSprite(this.bugemonOpponentImage, this.opponentBugemon.getSpritePath());
        this.bugemonOpponentImage.setOpacity(1.0);
    }

    private void setSprite(ImageView imageView, String spritePath) {
        File file = new File(Configuration.Paths.SPRITES + spritePath);
        imageView.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
    }

    private void initListeners() {
        this.initActionMenuListener();
        this.initAttackMenuListener();
        this.initSwitchMenuListener();
        this.initItemMenuListener();
    }

    private void initActionMenuListener() {
        this.actionMenu.setListener(new ActionMenuView.Listener() {
            @Override
            public void onAttack() {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onAttack();
                }
            }

            @Override
            public void onSwitch() {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onSwitch();
                }
            }

            @Override
            public void onInventory() {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onInventory();
                }
            }

            @Override
            public void onForfeit() {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onForfeit();
                }
            }
        });
    }

    private void initAttackMenuListener() {
        this.attackMenu.setListener(new AttackMenuView.Listener() {
            @Override
            public void onAttackChosen(Attack attack) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onAttackChosen(attack);
                }
            }

            @Override
            public void onAttackHovered(Attack attack) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onAttackHovered(attack);
                }
            }

            @Override
            public void onAttackUnhovered() {
                CombatView.this.hideAttackPreview();
            }

            @Override
            public void onBack() {
                CombatView.this.hideAttackPreview();
                CombatView.this.showMainActionMenu();
            }
        });
    }

    private void initSwitchMenuListener() {
        this.switchMenu.setListener(new SwitchMenuView.Listener() {
            @Override
            public void onSwitch(CombatBugemon bugemon) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onSwitchChosen(bugemon);
                }
            }

            @Override
            public void onBack() {
                CombatView.this.showMainActionMenu();
            }
        });
    }

    private void initItemMenuListener() {
        this.itemMenuView.setListener(new ItemMenuView.Listener() {
            @Override
            public void onItemChosen(Item item) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onItemChosen(item);
                }
            }

            @Override
            public void onItemHovered(Item item) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onItemHovered(item);
                }
            }

            @Override
            public void onItemUnhovered() {
                CombatView.this.hideAttackPreview();
            }

            @Override
            public void onBack() {
                CombatView.this.hideAttackPreview();
                CombatView.this.showMainActionMenu();
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setNextListener(NextListener nextlistener) {
        this.nextListener = nextlistener;
    }

    /**
     * Replaces the content of the action-menu slot with the given node.
     *
     * @param content
     *            the new menu component to display
     */
    protected void setActionMenuContent(Node content) {
        this.actionMenuSlot.getChildren().setAll(content);
    }

    /** Switches the action-menu slot back to the main four-button menu. */
    public void showMainActionMenu() {
        this.setActionMenuContent(this.actionMenu);
    }

    /**
     * Populates the attack menu with the given attacks and switches the action-menu slot to it.
     *
     * @param attacks
     *            the attacks available to the player's active Bugemon
     */
    public void showAttackMenu(List<Attack> attacks) {
        this.attackMenu.show(attacks);
        this.setActionMenuContent(this.attackMenu);
    }

    /**
     * Shows the hover info panel with attack details and the computed type-matchup efficiency badge.
     *
     * @param attack
     *            the attack being previewed
     * @param efficiency
     *            the type-matchup efficiency against the current opponent
     */
    public void showAttackPreview(Attack attack, Efficiency efficiency) {
        this.hoverInfoView.show(attack, efficiency);
    }

    /** Hides the hover info panel. */
    public void hideAttackPreview() {
        this.hoverInfoView.hide();
    }

    /**
     * Populates the switch menu with the available Bugemons and switches the action-menu slot to it.
     *
     * @param available
     *            Bugemons that can be switched in
     * @param forced
     *            {@code true} if this is a forced switch (no back button is shown)
     */
    public void showSwitchMenu(List<CombatBugemon> available, boolean forced) {
        this.switchMenu.show(available, forced);
        this.setActionMenuContent(this.switchMenu);
    }

    /**
     * Populates the item menu with the player's current inventory and switches the action-menu slot to it.
     *
     * @param inventory
     *            mapping of each item to its remaining quantity
     */
    public void showInventory(Map<Item, Integer> inventory) {
        this.itemMenuView.show(inventory);
        this.setActionMenuContent(this.itemMenuView);
    }

    /**
     * Shows the hover info panel with item details.
     *
     * @param item
     *            the item being previewed
     */
    public void showItemPreview(Item item) {
        this.hoverInfoView.show(item);
    }

    /** Hides the action-menu slot entirely (used while a dialog is visible). */
    protected void hideActionMenu() {
        this.actionMenuSlot.setVisible(false);
        this.actionMenuSlot.setManaged(false);
    }

    /** Restores the action-menu slot visibility after a dialog is dismissed. */
    protected void showActionMenu() {
        this.actionMenuSlot.setVisible(true);
        this.actionMenuSlot.setManaged(true);
    }

    /**
     * Displays the narration text and plays the animation for the given turn step.
     *
     * @param step
     *            the step to narrate and animate
     */
    public void showStep(TurnStep step) {
        this.showStepDialog(step);
        this.showStepAnimation(step);
    }

    private void showStepAnimation(TurnStep step) {
        switch (step) {
            case AttackStep s -> this.playAttackAnimation(s);
            case KoStep s -> this.playKoAnimation(s);
            default -> {
                // no animation for other steps
            }
        }
    }

    private void showStepDialog(TurnStep step) {
        this.hideAttackPreview();
        this.hideActionMenu();

        String message = switch (step) {
            case AttackStep a -> {
                String base = a.attacker().getName() + " utilise " + a.attack().name() + " ! ("
                        + a.damageResult().damage() + " dégâts)";
                yield switch (a.damageResult().efficiency()) {
                    case SUPER_EFFICIENT -> base + " C'est super efficace !";
                    case NOT_VERY_EFFICIENT -> base + " Ce n'est pas très efficace...";
                    case NORMAL -> base;
                };
            }
            case SwitchStep s ->
                (s.isPlayer() ? "Vous envoyez " : "L'adversaire envoie ") + s.bugemon().getName() + " !";
            case KoStep(CombatBugemon koBugemon) -> koBugemon.getName() + " est K.O. !";
            case HealBugemonStep(CombatBugemon healedBugemon, int hpAfterHeal) ->
                healedBugemon.getName() + " récupère des PV ! (Il a maintenant " + hpAfterHeal + " HP)";
            case HealTeamStep(CombatTeam healedTeam, int activeHpAfterHeal) -> "L'équipe " + healedTeam.getName()
                    + " récupère des PV ! ( Elle a maintenant " + activeHpAfterHeal + " HP)";
            case ItemStep(Item item) -> item.name() + " utilisé !";
            default -> "Action effectuée.";
        };

        this.showDialog(message);
    }

    private void showDialog(String dialog) {
        this.dialogZoneView.setNextButtonDisabled(false);
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), this.dialogZoneView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /** Hides the dialog zone and restores the action-menu slot. */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
        this.showActionMenu();
    }

    /**
     * Disables the Next button and hides the action menu, preventing any player input until the current animation or
     * step concludes.
     */
    public void lockNextButton() {
        this.dialogZoneView.setNextButtonDisabled(true);
        this.hideActionMenu();
    }

    /**
     * Plays the lunge-and-recoil sprite animation for an attack step, then re-enables the Next button when done. The
     * attacker slides toward the defender, triggers a red-glow flash on impact, and returns to its original position.
     */
    private void playAttackAnimation(TurnStep.AttackStep step) {
        boolean attackerIsAlly = (this.playerBugemon != null) && (step.attacker() == this.playerBugemon);
        ImageView attackerSprite = attackerIsAlly ? this.bugemonPlayerImage : this.bugemonOpponentImage;
        ImageView defenderSprite = attackerIsAlly ? this.bugemonOpponentImage : this.bugemonPlayerImage;
        double direction = attackerIsAlly ? 1.0 : -1.0;

        this.dialogZoneView.setNextButtonDisabled(true);

        TranslateTransition forward = new TranslateTransition(Duration.millis(300), attackerSprite);
        forward.setByX(direction * 40);

        TranslateTransition back = new TranslateTransition(Duration.millis(200), attackerSprite);
        back.setByX(direction * -40);
        back.setOnFinished(e -> this.dialogZoneView.setNextButtonDisabled(false));

        DropShadow redGlow = new DropShadow(20, Color.RED);
        redGlow.setSpread(0.8);
        forward.setOnFinished(e -> {
            defenderSprite.setEffect(redGlow);
            this.showDamagePopup(defenderSprite, step.damageResult().damage(), step.damageResult().efficiency());
            Timeline clearFlash = new Timeline(
                    new KeyFrame(Duration.millis(300), ev -> defenderSprite.setEffect(null)));
            clearFlash.play();
            back.play();
        });

        forward.play();
    }

    /**
     * Spawns a damage number that rises and fades above the struck sprite. Its size and colour reflect the type-matchup
     * efficiency (gold and larger when super-efficient, dimmed and smaller when resisted). No-ops for non-damaging hits
     * or when the root is not an overlay pane.
     */
    private void showDamagePopup(ImageView defenderSprite, int damage, Efficiency efficiency) {
        if (damage <= 0 || !(this.root instanceof StackPane overlay)) {
            return;
        }

        Label popup = new Label("-" + damage);
        popup.getStyleClass().add("damage-popup");
        switch (efficiency) {
            case SUPER_EFFICIENT -> popup.getStyleClass().add("damage-popup-super");
            case NOT_VERY_EFFICIENT -> popup.getStyleClass().add("damage-popup-weak");
            default -> {
                // normal efficiency keeps the base style
            }
        }
        popup.setMouseTransparent(true);
        popup.setVisible(false);
        StackPane.setAlignment(popup, Pos.TOP_LEFT);
        overlay.getChildren().add(popup);

        // Position once the label has been measured, reveal it, then float it upward and remove it when done.
        Platform.runLater(() -> {
            Bounds spriteBounds = overlay.sceneToLocal(defenderSprite.localToScene(defenderSprite.getBoundsInLocal()));
            popup.setTranslateX(spriteBounds.getMinX() + (spriteBounds.getWidth() - popup.getWidth()) / 2);
            popup.setTranslateY(spriteBounds.getMinY() + 10);
            popup.setVisible(true);

            TranslateTransition rise = new TranslateTransition(Duration.millis(900), popup);
            rise.setByY(-70);
            FadeTransition fade = new FadeTransition(Duration.millis(900), popup);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            ParallelTransition floatUp = new ParallelTransition(rise, fade);
            floatUp.setOnFinished(done -> overlay.getChildren().remove(popup));
            floatUp.play();
        });
    }

    /**
     * Fades the KO'd Bugemon's sprite to transparent, then re-enables the Next button.
     */
    private void playKoAnimation(TurnStep.KoStep step) {
        boolean bugemonIsAlly = (this.playerBugemon != null) && (step.koBugemon() == this.playerBugemon);
        ImageView sprite = bugemonIsAlly ? this.bugemonPlayerImage : this.bugemonOpponentImage;

        this.dialogZoneView.setNextButtonDisabled(true);

        FadeTransition fade = new FadeTransition(Duration.millis(500), sprite);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> this.dialogZoneView.setNextButtonDisabled(false));
        fade.play();
    }

    /** Callback interface for player turn acknowledgements (Next button clicks). */
    public interface NextListener {
        /** Called when the player clicks the Next button to advance the turn. */
        void onNext();
    }

    /** Callback interface for all combat action choices made by the player. */
    public interface Listener {

        /** Called when the player selects the Attack action. */
        void onAttack();

        /**
         * Called when the mouse enters an attack button.
         *
         * @param attack
         *            the hovered attack
         */
        void onAttackHovered(Attack attack);

        /**
         * Called when the player confirms an attack.
         *
         * @param attack
         *            the chosen attack
         */
        void onAttackChosen(Attack attack);

        /** Called when the player selects the Switch action. */
        void onSwitch();

        /**
         * Called when the player selects the Bugemon to switch in.
         *
         * @param bugemon
         *            the Bugemon chosen for the switch
         */
        void onSwitchChosen(CombatBugemon bugemon);

        /** Called when the player forfeits the combat. */
        void onForfeit();

        /** Called when the player opens the inventory. */
        void onInventory();

        /**
         * Called when the mouse enters an item button.
         *
         * @param item
         *            the hovered item
         */
        void onItemHovered(Item item);

        /**
         * Called when the player selects an item to use.
         *
         * @param item
         *            the chosen item
         */
        void onItemChosen(Item item);
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_VIEW;
    }

    @Override
    public void refresh() {
        // nothing to do
    }
}
