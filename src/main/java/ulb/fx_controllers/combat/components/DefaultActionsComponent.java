package ulb.fx_controllers.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class DefaultActionsComponent extends VBox {
    @FXML private Button attackButton;
    @FXML private Button itemButton;
    @FXML private Button switchButton;
    @FXML private Button forfeitButton;

    private CombatActionCallback onActionCallback;

    public DefaultActionsComponent() {
        // Initialize FXML and components
    }

    public void setOnActionCallback(CombatActionCallback callback) {
        this.onActionCallback = callback;
    }

    @FXML
    private void onAttack() {
        if (this.onActionCallback != null) {
            this.onActionCallback.onAction("attack");
        }
    }

    @FXML
    private void onItem() {
        if (this.onActionCallback != null) {
            this.onActionCallback.onAction("item");
        }
    }

    @FXML
    private void onSwitch() {
        if (this.onActionCallback != null) {
            this.onActionCallback.onAction("switch");
        }
    }

    @FXML
    private void onForfeit() {
        if (this.onActionCallback != null) {
            this.onActionCallback.onAction("forfeit");
        }
    }
}
