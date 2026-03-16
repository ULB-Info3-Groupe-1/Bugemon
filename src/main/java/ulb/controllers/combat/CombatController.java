package ulb.controllers.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.common.Efficiency;
import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.TurnResult;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.Trainer;
import ulb.services.LevelUpService;
import ulb.views.combat.CombatView;

/**
 * Abstract base controller for all combat screens.
 *
 * <p>
 * {@code CombatController} bridges the combat model ({@link ulb.models.combat.Combat})
 * and the combat view ({@link CombatView}) by providing shared logic that every
 * concrete combat controller needs:
 * <ul>
 *   <li>Navigating to the victory or defeat screen once a winner is known
 *       ({@link #handleCombatResult(Trainer, Trainer)}).</li>
 *   <li>Refreshing both sides of the combat UI after each turn
 *       ({@link #updateCombatView(Trainer, Trainer, Attack)}).</li>
 *   <li>Converting a raw {@link Efficiency} or {@link TurnResult.AttackResult}
 *       into a human-readable message
 *       ({@link #formatEfficiency(Efficiency)},
 *       {@link #formatEfficiency(TurnResult.AttackResult)}).</li>
 *   <li>Determining the type-effectiveness of an attack against a defender's
 *       type, delegating to
 *       {@link ulb.services.CombatService#compareBugemonType(ulb.models.bugemon.BugemonType,
 *       ulb.models.bugemon.BugemonType)}.</li>
 * </ul>
 *
 * <p>
 * Concrete subclasses ({@link AutomaticCombatController},
 * {@link ManualCombatController}) are responsible for driving the combat loop
 * and wiring user-interface events to model actions. They should call the
 * methods provided here rather than duplicating navigation or display logic.
 * </p>
 *
 * @param <View> the concrete {@link CombatView} subtype managed by this
 *               controller.
 *
 * @see AutomaticCombatController
 * @see ManualCombatController
 * @see CombatView
 * @see ulb.models.combat.Combat
 */
public abstract class CombatController<View extends CombatView> extends Controller<View> {
    // ── constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs a {@code CombatController} and associates it with the given
     * {@link MetaController} and {@link CombatView}.
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and shared state access; must not be
     *                       {@code null}.
     * @param view           the {@link CombatView} instance managed by this
     *                       controller; must not be {@code null}.
     */
    public CombatController(MetaController metaController, View view) {
        super(metaController, view);
    }

    // ── shared combat outcome handling ────────────────────────────────────────

    /**
     * Resolves the end of a combat session by distributing XP (on victory) and
     * navigating to the appropriate outcome screen.
     *
     * <p>
     * The navigation rules are:
     * <ul>
     *   <li>If {@code winner == player}, the player won: XP is distributed
     *       to every Bugemon that participated via
     *       {@link ulb.services.LevelUpService#distributeXp(Trainer, Trainer)}.
     *       Any resulting level-ups are
     *       forwarded to the {@link MetaController} which then navigates to
     *       {@link Window#LEVEL_UP} (or {@link Window#COMBAT_VICTORY} if no
     *       level-up occurred, depending on
     *       {@link MetaController#setLevelUp(List)}).</li>
     *   <li>If {@code winner != player}, the player lost: the application
     *       navigates directly to {@link Window#COMBAT_DEFEAT}.</li>
     * </ul>
     *
     * <p>
     * This method should be called by a subclass as soon as
     * {@link ulb.models.combat.Combat#isFinished()} returns {@code true} and
     * {@link ulb.models.combat.Combat#getWinner()} returns a non-empty
     * {@link java.util.Optional}.
     * </p>
     *
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

    protected void displayAttackResult(TurnResult.AttackResult firstAttackResult,
                                       Optional<TurnResult.AttackResult> secondAttackResult) {
        if (!firstAttackResult.wasAttack())
            return;
        // TODO : Loi de Déméter
        String message = "1- " + firstAttackResult.attacker().getCurrentBugemon().getName()
                         + " à utilisé l'attaque "
                         + firstAttackResult.attack().orElseThrow().getName() + "\n";
        String efficiency = "1- " + formatEfficiency(firstAttackResult.efficiency()) + "\n";

        if (secondAttackResult.isPresent()) {
            message += "2- " + secondAttackResult.get().attacker().getCurrentBugemon().getName()
                       + " à utilisé l'attaque "
                       + secondAttackResult.get().attack().orElseThrow().getName();
            efficiency += "2- " + formatEfficiency(secondAttackResult.get().efficiency());
        }
        view.showDialog(message, efficiency);
    }

    // ── efficiency helpers ────────────────────────────────────────────────────

    /**
     * Converts a raw {@link Efficiency} value into the human-readable French
     * message displayed in the combat dialogue zone.
     *
     * @param efficiency the {@link Efficiency} to convert; must not be
     *                   {@code null}.
     * @return a non-{@code null} string:
     *         {@code "ATTAQUE EFFICACE: félicitation"} for {@link Efficiency#HIGH},
     *         {@code "Peu d'effet ..."} for {@link Efficiency#LOW}, or
     *         {@code "Dégats standards"} for {@link Efficiency#NEUTRAL}.
     */
    protected String formatEfficiency(Efficiency efficiency) {
        switch (efficiency) {
            case HIGH:
                return "ATTAQUE EFFICACE: félicitation";
            case LOW:
                return "Peu d'effet ...";
            case NEUTRAL:
            default:
                return "Dégats standards";
        }
    }

    /**
     * Convenience overload of {@link #formatEfficiency(Efficiency)} that
     * extracts the efficiency from a {@link TurnResult.AttackResult}.
     *
     * <p>
     * Should only be called when {@link TurnResult.AttackResult#wasAttack()}
     * returns {@code true}; calling it on a non-attack result produces
     * undefined behaviour since {@link TurnResult.AttackResult#efficiency()}
     * will be {@code null}.
     * </p>
     *
     * @param result the {@link TurnResult.AttackResult} whose efficiency is to
     *               be formatted; must not be {@code null} and must represent
     *               an actual attack.
     * @return the formatted efficiency message; never {@code null}.
     */
    protected String formatEfficiency(TurnResult.AttackResult result) {
        return formatEfficiency(result.efficiency());
    }
}
