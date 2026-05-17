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
    private BugemonInfoView bugemonTrainerInfo;
    @FXML
    private BugemonInfoView bugemonOpponentInfo;
    @FXML
    private ImageView bugemonTrainerImage;
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
        this.attackAnimationView = new CombatAnimationView(this.bugemonTrainerImage, this.bugemonOpponentImage);
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

    private void refreshOpponent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshOpponent'");
    }

    private void refreshPlayer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshPlayer'");
    }

    private void initListeners() {
        this.actionMenu.setListener(new ActionMenuView.Listener() {
            @Override
            public void onAttack() {
                CombatView.this.showAttackMenu();
            }

            @Override
            public void onSwitch() {
                CombatView.this.showSwitchMenu(false);
            }

            @Override
            public void onInventory() {
                CombatView.this.showInventory();

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
            public void onAttack(Attack attack) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onAttack(attack);
                }
            }

            @Override
            public void onAttackHovered(Attack attack) {
                showHoverInfo(attack.name(), "Type : " + attack.type(), "Puissance : " + attack.power(),
                        attack.description().isBlank() ? null : attack.description());
            }

            @Override
            public void onAttackLeft() {
                CombatView.this.hideHoverInfo();
            }

            @Override
            public void onBack() {
                CombatView.this.hideHoverInfo();
                CombatView.this.showMainActionMenu();
            }
        });

        this.switchMenu.setListener(new SwitchMenuView.Listener() {
            @Override
            public void onSwitch(CombatBugemon bugemon) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onSwitch(bugemon);
                }
            }

            @Override
            public void onBack() {
                CombatView.this.showMainActionMenu();
            }
        });

        this.itemMenuView.setListener(new ItemMenuView.Listener() {
            @Override
            public void onItemSelected(Item item) {
                if (CombatView.this.listener != null) {
                    CombatView.this.listener.onItemSelected(item);
                }
            }

            @Override
            public void onItemHovered(Item item) {
                showHoverInfo(item.name(), "Catégorie : " + item.type(),
                        item.description().isBlank() ? null : item.description());
            }

            @Override
            public void onItemLeft() {
                CombatView.this.hideHoverInfo();
            }

            @Override
            public void onBack() {
                CombatView.this.hideHoverInfo();
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

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_VIEW;
    }

    public void refresh() {
        // if (this.playerTeam == null || this.opponentTeam == null) {
        //     return;
        // }
        //
        // this.updateTrainerBugemon(this.playerTeam.getActive());
        // this.updateOpponentBugemon(this.opponentTeam.getActive());
        // this.refreshMenuState();
    }

    // TODO: remove (logic in view)
    // public void refreshMenuState() {
    //     if (this.playerTeam == null) {
    //         return;
    //     }
    //
    //     if (this.playerTeam.getActive().isKo()) {
    //         this.showSwitchMenu(true);
    //     } else {
    //         boolean canSwitch = !this.playerTeam.getAvailable().isEmpty();
    //         this.actionMenu.refresh(canSwitch);
    //         this.showMainActionMenu();
    //     }
    // }

    protected void setActionMenuContent(Node content) {
        this.actionMenuSlot.getChildren().setAll(content);
    }

    public void showMainActionMenu() {
        this.setActionMenuContent(this.actionMenu);
    }

    private void showAttackMenu(List<Attack> attacks) {
        this.attackMenu.show(attacks);
        this.setActionMenuContent(this.attackMenu);
    }

    public void showSwitchMenu(List<CombatBugemon> available, boolean forced) {
        this.switchMenu.show(available, forced);
        this.setActionMenuContent(this.switchMenu);
    }

    private void showInventory(Map<Item, Integer> inventory) {
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

    public void showHoverInfo(String title, String... lines) {
        this.hoverInfoView.show(title, lines);
    }

    public void setHoverType(ElementType type) {
    }

    public void setHoverEfficiency(Efficiency eff) {
        this.hoverInfoView.setEfficiency(eff);
    }

    public void hideHoverInfo() {
        this.hoverInfoView.hide();
    }

    public void lockNextButton() {
        this.dialogZoneView.setNextButtonDisabled(true);
        this.hideActionMenu();
    }

    private void showDialog(String dialog) {
        this.dialogZoneView.setNextButtonDisabled(false);
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    public void showStepDialog(TurnStep step) {
        this.hideHoverInfo();
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

    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
        this.showActionMenu();
    }

    public void updateTrainerBugemon(CombatBugemon trainerBugemon) {
        if (!trainerBugemon.isKo()) {
            this.makeTrainerBugemonReappear();
        }
        File file = new File(Configuration.Paths.SPRITES + trainerBugemon.getSpritePath());
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
    }

    public void updateOpponentBugemon(CombatBugemon opponentBugemon) {
        if (!opponentBugemon.isKo()) {
            this.makeOpponentBugemonReappear();
        }
        File file = new File(Configuration.Paths.SPRITES + opponentBugemon.getSpritePath());
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
    }

    public void updateTrainerInfo(CombatBugemon bugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(bugemon);
    }

    public void updateOpponentInfo(CombatBugemon bugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(bugemon);
    }

    public void playTrainerAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playTrainerAttackAnimation(onFinished);
    }

    public void playOpponentAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playOpponentAttackAnimation(onFinished);
    }

    public void playDeathAnimationForTrainer(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForTrainer(onFinished);
    }

    public void playDeathAnimationForOpponent(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForOpponent(onFinished);
    }

    public void makeTrainerBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonTrainerImage);
    }

    public void makeOpponentBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonOpponentImage);
    }

    public interface NextListener {
        void onNext();
    }

    public interface Listener {
        void onAttack(Attack attack);

        void onSwitch(CombatBugemon bugemon);

        void onForfeit();

        void onItemSelected(Item item);
    }
}
