package ulb.fx_controllers.combat;

import java.util.List;

import javafx.fxml.FXML;
import ulb.common.dto.BugemonDTO;
import ulb.controllers.combat.ManualCombatController;
import ulb.fx_controllers.combat.components.AttackActionsComponent;
import ulb.fx_controllers.combat.components.CombatActionCallback;
import ulb.fx_controllers.combat.components.DefaultActionsComponent;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

public class ManualCombatFXController extends CombatFXController {
    @FXML private AttackActionsComponent attackActionComponent;
    @FXML private DefaultActionsComponent defaultActionComponent;

    private CombatActionCallback onActionCallback;

    public ManualCombatFXController(ManualCombatController controller) {
        super(controller);
        this.defaultActionComponent = new DefaultActionsComponent();
        this.attackActionComponent = new AttackActionsComponent();
        this.initCombatMode();
    }

    @Override
    public void initCombatMode() {
        showDefaultActionsMenu();
        hideBugemonTeamPane();
    }

    public void setOnActionCallback(CombatActionCallback callback) {
        this.onActionCallback = callback;
    }

    public void showDefaultActionsMenu() {
        this.actionMenuComponent.getChildren().setAll(this.defaultActionComponent);
        this.defaultActionComponent.setOnActionCallback(actionType -> {
            if (this.onActionCallback != null) {
                this.onActionCallback.onAction(actionType);
            }
        });
    }

    public void showAttackMenu(List<Attack> attacks, BugemonType opponentType) {
        this.attackActionComponent.setAttacks(attacks, opponentType);
        this.actionMenuComponent.getChildren().setAll(this.attackActionComponent);
        this.attackActionComponent.setOnActionCallback(actionType -> {
            if (actionType.startsWith("attack")) {
                // Handle attack action
                int attackIndex = Integer.parseInt(actionType.substring(6)) - 1;
                if (attackIndex >= 0 && attackIndex < attacks.size()) {
                    this.controller.onAttackSelected(attacks.get(attackIndex));
                }
            } else if (actionType.equals("return")) {
                // Handle return action
                showDefaultActionsMenu();
            }
        });
    }

    public void showSwitchMenu(List<BugemonDTO> bugemonList) {
        this.bugemonTeamPane.setVisible(true);
        this.bugemonTeamPane.setManaged(true);
        this.bugemonTeamView.setOnClickCallback(bugemon -> {
            if (bugemon != null) {
                this.controller.switchBugemon(bugemon.getId());
                hideSwitchPanel();
                showDefaultActionsMenu();
            }
        });
        this.bugemonTeamView.showTeam(bugemonList);
    }

    public void hideSwitchPanel() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    public void hideAllActionMenus() {
        this.actionMenuComponent.getChildren().clear();
    }
}
