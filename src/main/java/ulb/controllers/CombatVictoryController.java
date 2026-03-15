package ulb.controllers;

import ulb.controllers.MetaController.Window;
import ulb.services.LevelUpService;

public class CombatVictoryController extends Controller {
    private final LevelUpService levelUpService;

    public CombatVictoryController(MetaController metaController, LevelUpService levelUpService){
        super(metaController);
        this.levelUpService = levelUpService;
    }

    public void onContinue() {
        if (levelUpService.hasPendingLevelUp()) {
            this.metaController.switchTo(Window.LEVEL_UP);
        } else {
            this.metaController.switchTo(Window.MAIN_MENU);
        }
    }
}
