package ulb.controllers;

import java.io.IOException;
import java.util.List;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.AutomaticCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.views.CombatView;

public class CombatController extends Controller<CombatView> {

    private final List<Bugemon> bugemonList;

    public CombatController(
        MetaController metaController,
        List<Bugemon> bugemonList
    ) throws IOException {
        super(metaController, new CombatView());
        this.view.setController(this);

        this.bugemonList = bugemonList;
    }

    /**
     * Show the victory screen
     */
    public void handleVictory() {
        this.metaController.switchTo(Window.COMBAT_RESULT);
    }

    /**
     * Show the defeat screen
     */
    public void handleDefeat() {
        this.metaController.switchTo(Window.COMBAT_RESULT);
    }

    /**
     * Run a combat
     * @param playerTeam the team of the player
     */
    public void runCombat(final BugemonTeam playerTeam) {
        AutoTrainer player = new AutoTrainer(playerTeam);
        AutoTrainer opponent = new AutoTrainer(
            BugemonTeam.createRandomTeam(this.bugemonList, player.getTeamSize())
        );
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        AutoTrainer winner = null;
        while (winner == null) {
            combat.turn();
            winner = (AutoTrainer) combat.getWinner();
            combat.incrementTurn();
        }

        if (winner == player) {
            this.handleVictory();
        } else {
            this.handleDefeat();
        }
    }
}
