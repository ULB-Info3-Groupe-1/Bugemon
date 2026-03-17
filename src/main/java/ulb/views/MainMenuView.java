package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * View for the main menu screen.
 *
 * <p>
 * Dispatches user interactions to the controller exclusively through
 * callbacks registered via {@link #setOnCreateTeam(Runnable)} and
 * {@link #setOnQuit(Runnable)}. The view holds no reference to any
 * concrete controller class.
 * </p>
 */
public class MainMenuView extends View {
    @FXML private Button createTeamButton;
    @FXML private Button quitButton;

    private Runnable onCreateTeam;
    private Runnable onQuit;

    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
        this.createTeamButton.setOnAction(e -> { if (onCreateTeam != null) onCreateTeam.run(); });
        this.quitButton.setOnAction(e -> { if (onQuit != null) onQuit.run(); });
    }

    public void setOnCreateTeam(Runnable callback) {
        this.onCreateTeam = callback;
    }

    public void setOnQuit(Runnable callback) {
        this.onQuit = callback;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the main menu.
    }
}
