package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.bugemon.Attack;
import ulb.models.combat.ManualCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.models.trainer.ManualTrainer.TAction;
import ulb.views.combat.ManualCombatView;

public class ManualCombatController extends CombatController<ManualCombatView> {

    private ManualCombat combat;
    private ManualTrainer player;
    private AutoTrainer opponent;

    /**
     * Constructor of the ManualCombatController which initializes the view and sets the controller for the view
     * @param metaController The MetaController of the application
     * @throws IOException if the view cannot be initialized
     */
    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());
        this.view.setController(this);
    }

    /**
     * Run a manuel combat
     * @param playerTeam the team of the player
     */
    public void runManuelCombat(final ManualTrainer player) {
        this.player = player;
        this.opponent = new AutoTrainer(TeamFactory.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        this.combat = new ManualCombat(player, opponent);
        updateCombatView(player, opponent);
    }

    /**
     * Handle the player's attack action
     * @param attack the attack selected by the player
     */
    public void playerAttack(Attack attack) {
        System.out.println("Player attacks!");
        this.player.selectAttack(attack);
        this.player.selectAction(TAction.ATTACK);
        Trainer winner = this.combat.turn(this.player.getSelectedAction());
        handlePlayerTurn(winner);
    }

    /**
     * Handle the player's switch action
     */
    public void playerSwitch() {
        Trainer winner = this.combat.turn(TAction.SWITCH);
        handlePlayerTurn(winner);
    }

    /**
     * Handle the player's surrender action
     */
    public void surrender() {
        this.player.selectAction(TAction.FORFEIT);
        Trainer winner = this.combat.turn(this.player.getSelectedAction());
        handlePlayerTurn(winner);
    }

    /**
     * Handle the end of the player's turn, update the view and check if there is a winner
     */
    private void handlePlayerTurn(Trainer winner) {
        System.out.println("Player turn ended. Updating view...");
        updateCombatView(this.player, this.opponent);
        if (winner != null) {
            handleCombatResult(winner, player);
        }
    }

    /**
     * Show the attack menu to the player with the list of available attacks
     */
    public void showAttackMenu() {
        this.view.showAttackMenu(this.player.getCurrentBugemonAttackList());
    }

    /**
     * Show the main action menu to the player (Attack, Switch, Surrender)
     */
    public void showMainActionMenu() {
        this.view.showMainActionMenu();
    }

}