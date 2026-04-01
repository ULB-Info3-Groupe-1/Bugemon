package ulb.views.victory_view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.views.View;

/**
 * View for the combat defeat screen.
 *
 * <p>
 * Dispatches user interactions through the callbacks registered via {@link #setOnRetry(Runnable)}
 * and {@link #setOnBackToMainMenu(Runnable)}. Holds no reference to any concrete controller class.
 * </p>
 */
public class CombatDefeatView extends View {
    @FXML
    private Button retryButton;
    @FXML
    private Button backToMainMenuButton;

    private Runnable onRetry;
    private Runnable onBackToMainMenu;

    /**
     * Loads the defeat-screen FXML layout and wires the retry and back-to-menu buttons.
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public CombatDefeatView() throws IOException {
        super("/fxml/CombatDefeat.fxml");
        this.retryButton.setOnAction(e -> {
            if (this.onRetry != null) {
                this.onRetry.run();
            }
        });
        this.backToMainMenuButton.setOnAction(e -> {
            if (this.onBackToMainMenu != null) {
                this.onBackToMainMenu.run();
            }
        });
    }

    /** Registers the callback invoked when the player clicks the retry button. */
    public void setOnRetry(Runnable callback) {
        this.onRetry = callback;
    }

    /** Registers the callback invoked when the player clicks "retour au menu principal". */
    public void setOnBackToMainMenu(Runnable callback) {
        this.onBackToMainMenu = callback;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the defeat screen.
    }
}
