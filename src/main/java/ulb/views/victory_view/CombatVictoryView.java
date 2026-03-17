package ulb.views.victory_view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.views.View;

/**
 * View for the combat victory screen.
 *
 * <p>
 * Dispatches user interactions through the callback registered via
 * {@link #setOnContinue(Runnable)}. Holds no reference to any concrete
 * controller class.
 * </p>
 */
public class CombatVictoryView extends View {
    @FXML private Button continueButton;

    private Runnable onContinue;

    public CombatVictoryView() throws IOException {
        super("/fxml/CombatVictory.fxml");
        this.continueButton.setOnAction(e -> { if (onContinue != null) onContinue.run(); });
    }

    public void setOnContinue(Runnable callback) {
        this.onContinue = callback;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the victory screen.
    }
}
