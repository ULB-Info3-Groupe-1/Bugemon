package ulb.controllers.combat;


import ulb.controllers.Controller;
import ulb.controllers.MetaController;

import ulb.models.bugemon.Attack;

import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.LevelUpService;
import ulb.services.PlayerService;


public abstract class CombatController extends Controller {
    protected final PlayerService playerService;
    protected final LevelUpService levelUpService;

    protected boolean turnFinished = true;

    public CombatController(MetaController metaController, PlayerService playerService, LevelUpService levelUpService) {
        super(metaController);

        this.levelUpService = levelUpService;
        this.playerService = playerService;
    }

    public abstract void onContinue();

    public abstract void setupCombat();

    protected void handleCombatResult(Trainer winner, Trainer player) {
        // à refactor
    }

    public void updateCombatView(Trainer player, AutoTrainer opponent, Attack attack) {
        // à refactor
    }

    public void onAttackSelected(Attack attack) {}

    public void switchBugemon(String bugemonId) {}
}
