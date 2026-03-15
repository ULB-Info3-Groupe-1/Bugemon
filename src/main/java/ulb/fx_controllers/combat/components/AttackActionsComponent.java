package ulb.fx_controllers.combat.components;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ulb.common.Efficiency;
import ulb.fx_controllers.components.ActionMenuComponent;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.services.CombatService;

public class AttackActionsComponent extends ActionMenuComponent {
    private CombatActionCallback onActionCallback;
    private List<Attack> attacks;
    private BugemonType opponentType;

    public AttackActionsComponent() {
        super();
    }

    public void setAttacks(List<Attack> attacks, BugemonType opponentType) {
        this.attacks = attacks;
        this.opponentType = opponentType;
        updateAttackButtons();
    }

    private void updateAttackButtons() {
        if (attacks == null || attacks.isEmpty()) {
            return;
        }

        // Prepare action labels for the buttons
        List<String> actionLabels = attacks.stream()
                .map(attack -> {
                    Efficiency efficiency = CombatService.compareBugemonType(attack.getType(), opponentType);
                    return attack.getName() + "\n" + efficiency.toString();
                })
                .toList();

        // Set the actions using the parent class method
        setActions(actionLabels);

        // Add a "Return" button if there are fewer than 4 attacks
        if (attacks.size() < 4) {
            Button returnButton = new Button("Retour");
            returnButton.setPrefWidth(250.0);
            returnButton.setPrefHeight(100.0);
            returnButton.getStyleClass().add("action-button");

            HBox row = new HBox(10);
            row.getChildren().add(returnButton);

            actionContainer.getChildren().add(row);
        }
    }

    public void setOnActionCallback(CombatActionCallback callback) {
        this.onActionCallback = callback;
    }

    @Override
    public void setActions(List<String> actionLabels) {
        super.setActions(actionLabels);

        // Add event handlers to the buttons
        for (int i = 0; i < actionContainer.getChildren().size(); i++) {
            HBox row = (HBox) actionContainer.getChildren().get(i);
            for (int j = 0; j < row.getChildren().size(); j++) {
                Button button = (Button) row.getChildren().get(j);
                int attackIndex = i * 2 + j;
                if (attackIndex < attacks.size()) {
                    button.setOnAction(event -> {
                        if (onActionCallback != null) {
                            onActionCallback.onAction("attack" + (attackIndex + 1));
                        }
                    });
                } else {
                    // Handle the "Return" button
                    button.setOnAction(event -> {
                        if (onActionCallback != null) {
                            onActionCallback.onAction("return");
                        }
                    });
                }
            }
        }
    }
}
