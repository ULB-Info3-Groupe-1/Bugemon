package ulb.controllers;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.ManualCombatController;
import ulb.fx_controllers.combat.AutomaticCombatFXController;
import ulb.fx_controllers.combat.ManualCombatFXController;
import ulb.fx_controllers.combat_result.CombatVictoryFXController;
import ulb.fx_controllers.MainMenuFXController;
import ulb.fx_controllers.CreateTeamFXController;
import ulb.fx_controllers.LevelUpFXController;
import ulb.services.LevelUpService;
import ulb.services.PlayerService;

public class ControllerFactory {
    private final MetaController metaController;
    private final PlayerService playerService;
    private final LevelUpService levelUpService;

    public ControllerFactory(MetaController metaController, PlayerService playerService, LevelUpService levelUpService) {
        this.metaController = metaController;
        this.playerService = playerService;
        this.levelUpService = levelUpService;
    }

    // Create real controllers
    public CreateTeamController createCreateTeamController() {
        return new CreateTeamController(this.metaController, this.playerService);
    }

    public AutomaticCombatController createAutomaticCombatController() {
        return new AutomaticCombatController(this.metaController, this.playerService, this.levelUpService);
    }

    public ManualCombatController createManualCombatController() {
        return new ManualCombatController(this.metaController, this.playerService, this.levelUpService);
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
