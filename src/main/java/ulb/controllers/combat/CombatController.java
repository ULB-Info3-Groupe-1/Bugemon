package ulb.controllers.combat;

import java.util.List;
import java.util.function.Consumer;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.factory.TeamFactory;
import ulb.models.combat.TurnResult;
import ulb.models.level_up.LevelUp;
import ulb.models.player.Player;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.LevelUpService;
import ulb.utils.Parser;
import ulb.views.combat.CombatView;

/**
 * Abstract base controller for all combat screens.
 *
 * <p>
 * Provides the shared {@link #handleCombatResult(Trainer, Trainer)} method that
 * distributes XP and navigates to the correct outcome screen. Concrete
 * subclasses drive the combat loop and call {@code view.refresh()} after each
 * model mutation; they never push data into the view directly.
 * </p>
 *
 * @param <V> the concrete {@link CombatView} subtype managed by this
 *            controller.
 */
public abstract class CombatController<V extends CombatView> extends Controller<V> {
    private Consumer<List<LevelUp>> onVictory;
    protected final Player player;

    public CombatController(MetaController metaController, V view, Player player) {
        super(metaController, view);
        this.player = player;
    }

    public void setOnVictory(Consumer<List<LevelUp>> onVictory) {
        this.onVictory = onVictory;
    }

    public abstract void startCombat();

    /**
     * Plays the attack animations contained in a turn result, then invokes
     * {@code onFinished}. If the turn has no attacks, the callback is executed
     * immediately.
     *
     * @param result        the turn result containing the attacks to animate.
     * @param playerTrainer the player's trainer, used to determine animation
     *                      direction.
     * @param onFinished    the callback to execute after all animations have
     *                      played.
     */
    protected void playTurnAnimations(TurnResult result, Trainer playerTrainer,
                                      Runnable onFinished) {
        if (result == null || !result.first().wasAttack()) {
            onFinished.run();
            return;
        }

        boolean firstFromPlayer = result.first().attacker() == playerTrainer;
        this.view.playAttackAnimation(firstFromPlayer, () -> {
            if (result.second().isPresent() && result.second().orElseThrow().wasAttack()) {
                boolean secondFromPlayer =
                        result.second().orElseThrow().attacker() == playerTrainer;
                this.view.playAttackAnimation(secondFromPlayer, onFinished);
            } else {
                onFinished.run();
            }
        });
     * @param winner the {@link Trainer} that won the combat; must not be
     *               {@code null}.
     * @param player the {@link Trainer} representing the local player, used to
     *               determine whether the outcome is a victory or a defeat; must
     *               not be {@code null}.
     */
    protected void handleCombatResult(Trainer winner, Trainer player) {
        List<LevelUp> levelUps = new ArrayList<>();
        if (winner == player) {
            // TODO: Make sure to show xp gained after combat.
            // TODO: Do not forget to set manually floor and multiplier based on NO combat.
            int xp = LevelUpService.distributeXp(winner, player);

            List<Bugemon> participatingBugemons =
                    winner.getTeam().stream().filter(b -> b.getParticipation()).toList();

            levelUps = LevelUpService.levelUp(participatingBugemons);
            this.metaController.setLevelUp(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }

    // ── view update helpers ───────────────────────────────────────────────────

    /**
     * Refreshes the combat view with the current state of both trainers and,
     * optionally, displays a type-efficiency message for the given attack.
     *
     * <p>
     * The dialog zone is first hidden, then both Bugemon info panels (HP bar,
     * sprite, name) are updated. If {@code attack} is non-{@code null}, the
     * type-effectiveness of the attack against the opponent's active Bugemon is
     * computed via
     * {@link ulb.services.CombatService#compareBugemonType(ulb.models.bugemon.BugemonType,
     * ulb.models.bugemon.BugemonType)} and a corresponding message is shown
     * via {@link CombatView#showDialog(String, String)}.
     * </p>
     *
     * @param player   the player-side {@link Trainer} whose active Bugemon info
     *                 is rendered on the left panel; must not be {@code null}.
     * @param opponent the opponent-side {@link Trainer} whose active Bugemon info
     *                 is rendered on the right panel; must not be {@code null}.
     * @param attack   the {@link Attack} whose type-effectiveness should be
     *                 displayed, or {@code null} to skip the dialogue entirely.
     */
    public void updateCombatView(Trainer player, Trainer opponent, Attack attack) {
        this.view.hideDialog();
        this.view.updateTrainerBugemon(player.getCurrentBugemon());
        this.view.updateOpponentBugemon(opponent.getCurrentBugemon());
    }

    protected void displayAttackResult(TurnResult.AttackResult attackResult) {
        if (!attackResult.wasAttack())
            return;
        // TODO : Loi de Déméter
        String message = attackResult.attacker().getCurrentBugemon().getName()
                         + " à utilisé l'attaque "
                         + attackResult.attack().orElseThrow().getName();
        String efficiency = formatEfficiency(attackResult.efficiency());
        view.showDialog(message, efficiency);
    }

    /**
     * Creates a random opponent team sized to match the given player's team.
     *
     * @param playerTeamSize the size of the player's team, used to size the
     *                       opponent's team.
     *
     * @return an {@link AutoTrainer} with a randomly generated team.
     */
    protected AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(
                TeamFactory.createRandomTeam(Parser.getInstance().getBugemons(), playerTeamSize));
    }

    /**
     * Resolves the end of a combat session by distributing XP on victory and
     * navigating to the appropriate outcome screen.
     *
     * @param winner        the winning trainer, used to determine if the player won
     *                      or lost.
     * @param playerTrainer the player's trainer, used to determine if the player
     *                      won
     *                      or lost and to distribute XP on victory.
     */
    protected void handleCombatResult(Trainer winner, Trainer playerTrainer) {
        if (winner == playerTrainer) {
            List<LevelUp> levelUps =
                    LevelUpService.distributeXpAndGetLevelUps(winner, playerTrainer);

            this.onVictory.accept(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }
}
