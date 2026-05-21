package ulb.views.combat;

import java.io.File;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.damage.Efficiency;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
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

// TODO: remove magic numbers

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

    public void displayBugemons(CombatBugemon newPlayerBugemon, CombatBugemon newOpponentBugemon) {
        this.playerBugemon = newPlayerBugemon;
        this.opponentBugemon = newOpponentBugemon;
        this.refreshPlayer();
        this.refreshOpponent();

        this.hideDialog();
    }

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

    public void updateHp(CombatBugemon bugemon, int currentHp) {
        if (bugemon.equals(this.playerBugemon)) {
            this.bugemonPlayerInfo.setHp(currentHp, bugemon.getMaxHp());
        } else if (bugemon.equals(this.opponentBugemon)) {
            this.bugemonOpponentInfo.setHp(currentHp, bugemon.getMaxHp());
        }
    }

    private void refreshPlayer() {
        this.bugemonPlayerInfo.setBugemonInfo(this.playerBugemon);
        this.setSprite(this.bugemonPlayerImage, this.playerBugemon.getSpritePath());
    }

    private void refreshOpponent() {
        this.bugemonOpponentInfo.setBugemonInfo(this.opponentBugemon);
        this.setSprite(this.bugemonOpponentImage, this.opponentBugemon.getSpritePath());
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

    protected void setActionMenuContent(Node content) {
        this.actionMenuSlot.getChildren().setAll(content);
    }

    public void showMainActionMenu() {
        this.setActionMenuContent(this.actionMenu);
    }

    public void showAttackMenu(List<Attack> attacks) {
        this.attackMenu.show(attacks);
        this.setActionMenuContent(this.attackMenu);
    }

    public void showAttackPreview(Attack attack, Efficiency efficiency) {
        this.hoverInfoView.show(attack, efficiency);
    }

    public void hideAttackPreview() {
        this.hoverInfoView.hide();
    }

    public void showSwitchMenu(List<CombatBugemon> available, boolean forced) {
        this.switchMenu.show(available, forced);
        this.setActionMenuContent(this.switchMenu);
    }

    public void showInventory(Map<Item, Integer> inventory) {
        this.itemMenuView.show(inventory);
        this.setActionMenuContent(this.itemMenuView);
    }

    public void showItemPreview(Item item) {
        this.hoverInfoView.show(item);
    }

    protected void hideActionMenu() {
        this.actionMenuSlot.setVisible(false);
        this.actionMenuSlot.setManaged(false);
    }

    protected void showActionMenu() {
        this.actionMenuSlot.setVisible(true);
        this.actionMenuSlot.setManaged(true);
    }

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
            default -> "Action effectuée.";
        };

        this.showDialog(message);
    }

    private void showDialog(String dialog) {
        this.dialogZoneView.setNextButtonDisabled(false);
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
        this.showActionMenu();
    }

    public void lockNextButton() {
        this.dialogZoneView.setNextButtonDisabled(true);
        this.hideActionMenu();
    }

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
            Timeline clearFlash = new Timeline(
                    new KeyFrame(Duration.millis(300), ev -> defenderSprite.setEffect(null)));
            clearFlash.play();
            back.play();
        });

        forward.play();
    }

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

    public interface NextListener {
        void onNext();
    }

    public interface Listener {
        void onAttack();

        void onAttackHovered(Attack attack);

        void onAttackChosen(Attack attack);

        void onSwitch();

        void onSwitchChosen(CombatBugemon bugemon);

        void onForfeit();

        void onInventory();

        void onItemHovered(Item item);

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
