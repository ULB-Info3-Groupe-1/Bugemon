package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * MainMenuView
 *
 * View for the main menu screen.
 */
public class MainMenuView extends View {

    @FXML private Button createTeamButton;
    @FXML private Button quitButton;

    /**
     * Loads the main menu FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
     }

     /**
      * Sets the action to be performed when the create team button is clicked.
      * @param action the Runnable to execute on create team button click
      */
     public void setCreateTeamButtonAction(Runnable action) {
        this.createTeamButton.setOnAction(e -> action.run());
     }

     /**
      * Sets the action to be performed when the quit button is clicked.
      * @param action the Runnable to execute on quit button click
      */
     public void setQuitButtonAction(Runnable action) {
        this.quitButton.setOnAction(e -> action.run());
     }

}
