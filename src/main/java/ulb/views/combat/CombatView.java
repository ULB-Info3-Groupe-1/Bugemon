package ulb.views.combat;

import java.io.File;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
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

public class CombatView extends View {

    private CombatAnimationView attackAnimationView;

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
        this.attackAnimationView = new CombatAnimationView(this.bugemonPlayerImage, this.bugemonOpponentImage);
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

        } else {
            this.opponentBugemon = switchStep.bugemon();

        }
    }

    public void updateHp(CombatBugemon bugemon, int currentHp) {
        if (bugemon.equals(this.playerBugemon)) {
            this.bugemonPlayerInfo.setHp(currentHp, bugemon.getMaxHp());
        } else if (bugemon.equals(this.opponentBugemon)) {
            this.bugemonOpponentInfo.setHp(currentHp, bugemon.getMaxHp());
        }
    }

    private void refreshOpponent() {
        this.bugemonPlayerInfo.setBugemonInfo(this.playerBugemon);
        this.setSprite(this.bugemonPlayerImage, this.playerBugemon.getSpritePath());
    }

    private void refreshPlayer() {
        this.bugemonOpponentInfo.setBugemonInfo(this.opponentBugemon);
        this.setSprite(this.bugemonOpponentImage, this.opponentBugemon.getSpritePath());
    }

    private void setSprite(ImageView imageView, String spritePath) {
        File file = new File(Configuration.Paths.SPRITES + spritePath);
        imageView.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
    }

    private void initListeners() {
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

    public void setNextListener(NextListener listener) {
        this.nextListener = listener;
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

    public void showSwitchMenu(List<CombatBugemon> available, boolean forced) {
        this.switchMenu.show(available, forced);
        this.setActionMenuContent(this.switchMenu);
    }

    public void showInventory(Map<Item, Integer> inventory) {
        this.itemMenuView.show(inventory);
        this.setActionMenuContent(this.itemMenuView);
    }

    protected void hideActionMenu() {
        this.actionMenuSlot.setVisible(false);
        this.actionMenuSlot.setManaged(false);
    }

    protected void showActionMenu() {
        this.actionMenuSlot.setVisible(true);
        this.actionMenuSlot.setManaged(true);
    }

    public void showStepDialog(TurnStep step) {
        this.hideAttackPreview();
        this.hideActionMenu();

        String message = switch (step) {
            case AttackStep a -> a.attacker().getName() + " utilise " + a.attack().name() + " !";
            case SwitchStep s ->
                (s.isPlayer() ? "Vous envoyez " : "L'adversaire envoie ") + s.bugemon().getName() + " !";
            case KoStep k -> k.koBugemon().getName() + " est K.O. !";
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

    public void showAttackPreview(Attack attack, Efficiency efficiency) {
        this.hoverInfoView.show(attack, efficiency);
    }

    public void hideAttackPreview() {
        this.hoverInfoView.hide();
    }

    public void lockNextButton() {
        this.dialogZoneView.setNextButtonDisabled(true);
        this.hideActionMenu();
    }

    public void updatePlayerBugemon(CombatBugemon playerBugemon) {
        if (!playerBugemon.isKo()) {
            this.makePlayerBugemonReappear();
        }
        this.bugemonPlayerInfo.setBugemonInfo(playerBugemon);
        this.setSprite(this.bugemonPlayerImage, playerBugemon.getSpritePath());
    }

    public void playPlayerAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playPlayerAttackAnimation(onFinished);
    }

    public void playOpponentAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playOpponentAttackAnimation(onFinished);
    }

    public void playDeathAnimationForPlayer(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForPlayer(onFinished);
    }

    public void playDeathAnimationForOpponent(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForOpponent(onFinished);
    }

    public void makePlayerBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonPlayerImage);
    }

    public void makeOpponentBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonOpponentImage);
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
    }
}
