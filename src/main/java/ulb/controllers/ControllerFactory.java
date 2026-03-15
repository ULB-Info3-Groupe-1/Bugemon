package ulb.controllers;

import java.io.IOException;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.ManualCombatController;
import ulb.fx_controllers.*;
import ulb.services.LevelUpService;
import ulb.services.TeamService;

public class ControllerFactory {
    private final MetaController metaController;
    private final TeamService teamService;
    private final LevelUpService levelUpService;

    public ControllerFactory(MetaController metaController, TeamService teamService, LevelUpService levelUpService) {
        this.metaController = metaController;
        this.teamService = teamService;
        this.levelUpService = levelUpService;
    }

    // Create real controllers
    public MainMenuController createMainMenuController() {
        return new MainMenuController(this.metaController);
    }

    public CreateTeamController createCreateTeamController() {
        return new CreateTeamController(this.metaController, this.teamService);
    }

    public AutomaticCombatController createAutomaticCombatController() {
        return new AutomaticCombatController(this.metaController, this.teamService, this.levelUpService);
    }

    public ManualCombatController createManualCombatController() {
        return new ManualCombatController(this.metaController, this.teamService, this.levelUpService);
    }

    public CombatVictoryController createCombatVictoryController() {
        return new CombatVictoryController(this.metaController, this.levelUpService);
    }

    public LevelUpController createLevelUpController() {
        return new LevelUpController(this.metaController, this.levelUpService);
    }

    // Create FXControllers
    public MainMenuFXController createMainMenuFXController() {
        return new MainMenuFXController();
    }

    public CreateTeamFXController createCreateTeamFXController() {
        CreateTeamController createTeamController = createCreateTeamController();
        return new CreateTeamFXController(createTeamController);
    }

    public AutomaticCombatFXController createAutomaticCombatFXController() {
        AutomaticCombatController automaticCombatController = createAutomaticCombatController();
        return new AutomaticCombatFXController(automaticCombatController);
    }

    public ManualCombatFXController createManualCombatFXController() {
        ManualCombatController manualCombatController = createManualCombatController();
        return new ManualCombatFXController(manualCombatController);
    }

    public CombatVictoryFXController createCombatVictoryFXController() {
        CombatVictoryController combatVictoryController = createCombatVictoryController();
        return new CombatVictoryFXController(combatVictoryController);
    }

    public LevelUpFXController createLevelUpFXController() {
        LevelUpController levelUpController = createLevelUpController();
        return new LevelUpFXController(levelUpController);
    }
}
