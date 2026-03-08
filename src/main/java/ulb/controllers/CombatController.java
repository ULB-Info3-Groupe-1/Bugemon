package ulb.controllers;

import java.util.ArrayList;
import java.util.List;
import ulb.controllers.MetaController.Window;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.AutomaticCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.views.combat.CombatView;

public abstract class CombatController<View extends CombatView> extends Controller<View> {
    
    public CombatController(MetaController metaController, View view) {
        super(metaController, view);

        Bugemon bugemon1 = new Bugemon.Builder()
            .id("id1")
            .build();

        Bugemon bugemon2 = new Bugemon.Builder()
            .id("id2")
            .build();

        this.view.updateTrainerBugemon(bugemon1);
        this.view.updateOpponentBugemon(bugemon2);

        this.view.initCombatMode();
    }

    /**
     * Show the victory screen
     */
    public void handleVictory() {
        this.metaController.switchTo(Window.COMBAT_VICTORY);
    }

    /**
     * Show the defeat screen
     */
    public void handleDefeat() {
        this.metaController.switchTo(Window.COMBAT_DEFEAT);
    }

    /**
     * Run a combat
     * 
     * @param playerTeam the team of the player
     */
    public void runCombat(final BugemonTeam playerTeam) {
        // AutoTrainer player = new AutoTrainer(playerTeam);
        // AutoTrainer opponent = new AutoTrainer(BugemonTeam.createRandomTeam(this.bugemonList, player.getTeamSize()));
        // AutomaticCombat combat = new AutomaticCombat(player, opponent);

        // AutoTrainer winner = null;
        // while (winner == null) {
        //     winner = combat.turn();
        // }

        // if (winner == player) {this.handleVictory();}
        // else {this.handleDefeat();}
    }
}
