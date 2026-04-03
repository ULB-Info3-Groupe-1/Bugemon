package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * View for the combat defeat screen.
 *
 * <p>
 * Dispatches user interactions through the callbacks registered via {@link #setOnRetry(Runnable)} and
 * {@link #setOnBackToMainMenu(Runnable)}. Holds no reference to any concrete controller class.
 * </p>
 */
public class CombatDefeatView extends View {
    @FXML
    private Button retryButton;
    @FXML
    private Button backToMainMenuButton;

    Listener listener;

    /**
     * Loads the defeat-screen FXML layout and wires the retry and back-to-menu buttons.
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public CombatDefeatView() throws IOException {
        super("/fxml/CombatDefeat.fxml");
        this.retryButton.setOnAction(e -> {
            this.listener.onRetry();
        });
        this.backToMainMenuButton.setOnAction(e -> {
            this.listener.onBackToMainMenu();
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the defeat screen.
    }

    public interface Listener {

        void onRetry();

        void onBackToMainMenu();

    }
}
