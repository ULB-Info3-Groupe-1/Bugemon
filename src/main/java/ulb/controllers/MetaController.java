package ulb.controllers;

import java.io.IOException;

import javafx.stage.Stage;

public class MetaController {

    public enum Window {
        MAIN_MENU,
        CREATE_TEAM,
        COMBAT,
        COMBAT_RESULT,
    }

    private final Stage stage;
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final CombatController combatController;
    private final CombatResultController combatResultController;

    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this);
        this.combatController = new CombatController(this);
        this.combatResultController = new CombatResultController(this);
    }

    public final void switchTo(Window window) {
        switch (window) {
            case MAIN_MENU ->
                this.mainMenuController.show(this.stage);
            case CREATE_TEAM ->
                this.createTeamController.show(this.stage);
            case COMBAT ->
                this.combatController.show(this.stage);
            case COMBAT_RESULT ->
                this.combatResultController.show(this.stage);
            default ->
                throw new IllegalArgumentException("Invalid window");
        }
    }

}
