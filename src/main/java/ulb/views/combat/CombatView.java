package ulb.views.combat;

import java.io.IOException;
import java.util.Optional;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import ulb.common.Efficiency;
import ulb.common.dto.BugemonDTO;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.Trainer;
import ulb.views.DialogZoneView;
import ulb.views.View;

/**
 * Abstract base view for all combat screens.
 *
 * <p>
 * {@code CombatView} loads the shared {@code Combat.fxml} layout and exposes
 * the FXML-injected components that are common to every combat mode:
 * Bugemon info panels, sprite images, the action menu container, the team
 * switcher pane and the dialog zone.
 * </p>
 *
 * <p>
 * Concrete subclasses ({@link AutomaticCombatView}, {@link ManualCombatView})
 * must implement {@link #initCombatMode()} to configure which UI regions are
 * visible and how they behave for their specific mode.
 * </p>
 *
 * <p>
 * The controller layer interacts with the combat UI exclusively through the
 * public methods of this class, keeping all JavaFX node manipulation out of
 * the controller.
 * </p>
 *
 * @see AutomaticCombatView
 * @see ManualCombatView
 */
public abstract class CombatView extends View {
    private static final double ATTACK_LUNGE_DISTANCE = 100;
    private static final Duration ATTACK_LUNGE_DURATION = Duration.millis(150);
    private int lastAnimatedTurn = -1;

    // ── FXML-injected components ──────────────────────────────────────────────

    /**
     * Info panel (name, type, HP bar) for the player's active Bugemon,
     * displayed on the player's side of the combat screen.
     */
    @FXML
    protected BugemonInfoView bugemonTrainerInfo;

    /**
     * Info panel (name, type, HP bar) for the opponent's active Bugemon,
     * displayed on the opponent's side of the combat screen.
     */
    @FXML
    protected BugemonInfoView bugemonOpponentInfo;

    /** Sprite image of the player's currently active Bugemon. */
    @FXML
    protected ImageView bugemonTrainerImage;

    /** Sprite image of the opponent's currently active Bugemon. */
    @FXML
    protected ImageView bugemonOpponentImage;

    /**
     * Container for the action menu components (main menu, attack menu, …).
     * Subclasses populate this container via their own menu components.
     */
    @FXML
    protected ActionMenuView actionMenuView;

    /**
     * Overlay banner used to display turn feedback messages such as attack
     * effectiveness or KO notifications. Toggled visible/invisible by
     * {@link #showDialog(String, String)} and {@link #hideDialog()}.
     */
    @FXML
    protected DialogZoneView dialogZoneView;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Loads the shared {@code Combat.fxml} layout.
     *
     * <p>
     * Subclass constructors must call {@code super()} and then invoke
     * {@link #initCombatMode()} to finalise their mode-specific UI setup.
     * </p>
     *
     * @throws IOException if the {@code Combat.fxml} resource cannot be found
     *                     or parsed.
     */
    public CombatView() throws IOException {
        super("/fxml/Combat.fxml");
    }

    // ── Abstract contract ─────────────────────────────────────────────────────

    /**
     * Configures the combat UI for the specific mode implemented by the
     * subclass.
     *
     * <p>
     * Typical implementations show or hide regions that are irrelevant for
     * their mode (e.g. {@link AutomaticCombatView} hides the action menu and
     * team pane, while {@link ManualCombatView} shows the main action menu).
     * This method is called once by the subclass constructor after the FXML
     * components have been injected.
     * </p>
     */
    protected abstract void initCombatMode();

    // ── Dialog zone ───────────────────────────────────────────────────────────

    /**
     * Displays the dialog zone with the given message and optional additional
     * information.
     *
     * <p>
     * Typical uses include showing attack-effectiveness feedback
     * ("ATTAQUE EFFICACE !") or KO announcements. The dialog zone remains
     * visible until {@link #hideDialog()} is called.
     * </p>
     *
     * @param dialog         the main message to display; must not be
     *                       {@code null}.
     * @param additionalInfo a secondary line of text, or {@code null} if no
     *                       additional information should be shown.
     */
    public void showDialog(String dialog, String additionalInfo) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setAdditionalInfo(additionalInfo);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Builds and displays the turn-summary dialog from the two attack results of
     * the last resolved turn. Only the attacks that actually happened are shown;
     * the second result is absent when one trainer did not attack.
     *
     * @param firstAttackResult  result of the first attack; never {@code null}.
     * @param secondAttackResult result of the second attack, or empty if only
     *                           one attack was made this turn.
     */
    public void showCombatDialog(TurnResult.AttackResult firstAttackResult,
            Optional<TurnResult.AttackResult> secondAttackResult) {
        StringBuilder message = new StringBuilder();
        StringBuilder efficiency = new StringBuilder();

        appendAttackLine(message, efficiency, 1, firstAttackResult);
        secondAttackResult.ifPresent(second -> appendAttackLine(message, efficiency, 2, second));

        if (message.length() == 0) {
            hideDialog();
            return;
        }

        showDialog(message.toString(), efficiency.toString());
    }

    /**
     * Converts an {@link Efficiency} value to a human-readable French label.
     * 
     * @param efficiency the efficiency value to format; must not be {@code null}.
     * @return a user-friendly string describing the efficiency value, in French.
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
     * Refreshes sprite/info panels and turn dialog, playing attack animations
     * exactly once per resolved combat turn.
     * 
     * @param combat   the current {@link Combat} model; must not be {@code null}.
     * @param trainer  the player's {@link Trainer} model; must not be {@code null}.
     * @param opponent the opponent's {@link Trainer} model; must not be
     *                 {@code null}.
     */
    protected void refreshCombatTurn(Combat combat, Trainer trainer, Trainer opponent) {
        if (combat == null || trainer == null || opponent == null) {
            return;
        }

        TurnResult lastTurn = combat.getLastTurnResult();
        int turn = combat.getTurn();

        if (shouldAnimateTurn(turn, lastTurn)) {
            animateTurn(lastTurn, trainer, opponent,
                    () -> {
                        renderCombatState(trainer, opponent, lastTurn);
                    });
            lastAnimatedTurn = turn;
            return;
        }

        renderCombatState(trainer, opponent, lastTurn);
    }

    /**
     * Determines whether the turn dialog should be animated for the given turn.
     * 
     * @param turn     the current combat turn number.
     * @param lastTurn the result of the last resolved turn, or {@code null} if no
     *                 turn has been
     * @return {@code true} if the turn dialog should be animated, {@code false} to
     *         just refresh the display without animation.
     */
    private boolean shouldAnimateTurn(int turn, TurnResult lastTurn) {
        return lastTurn != null && turn > 0 && turn != lastAnimatedTurn && hasAttack(lastTurn);
    }

    /**
     * Updates the combat view to reflect the current state of the combat after a
     * turn
     * has been resolved, and shows the turn dialog if at least one attack occurred.
     * 
     * @param trainer  The player's {@link Trainer} model; must not be {@code null}.
     * @param opponent The opponent's {@link Trainer} model; must not be
     *                 {@code null}.
     * @param lastTurn the result of the last resolved turn, or {@code null} if no
     *                 turn has been resolved yet.
     */
    private void renderCombatState(Trainer trainer, Trainer opponent, TurnResult lastTurn) {
        updateTrainerBugemon(trainer.getCurrentBugemon());
        updateOpponentBugemon(opponent.getCurrentBugemon());

        if (lastTurn != null && hasAttack(lastTurn)) {
            showCombatDialog(lastTurn.first(), lastTurn.second());
        } else {
            hideDialog();
        }
    }

    /**
     * Determines whether at least one attack occurred during the given turn.
     * 
     * @param turnResult the result of the turn to check; must not be {@code null}.
     * @return {@code true} if at least one attack occurred during the turn,
     *         {@code false}
     */
    private boolean hasAttack(TurnResult turnResult) {
        return turnResult.first().wasAttack()
                || turnResult.second().map(TurnResult.AttackResult::wasAttack).orElse(false);
    }

    /**
     * Plays the attack animations for the given turn result, then updates the
     * combat view to reflect the current state of the combat.
     * 
     * @param turnResult the result of the turn to animate; must not be
     *                   {@code null}.
     * @param trainer    the player's {@link Trainer} model; must not be
     *                   {@code null}.
     * @param opponent   the opponent's {@link Trainer} model; must not be
     *                   {@code null}.
     * @param onFinished callback executed once the animations complete; must not be
     *                   {@code null}.
     */
    private void animateTurn(TurnResult turnResult, Trainer trainer, Trainer opponent,
            Runnable onFinished) {
        Runnable playSecond = () -> turnResult.second().ifPresentOrElse(
                second -> animateAttack(second, trainer, opponent, onFinished), onFinished);

        animateAttack(turnResult.first(), trainer, opponent, playSecond);
    }

    /**
     * Plays the attack animation for a single attack result, then invokes
     * {@code onFinished}.
     * 
     * @param attackResult the attack result to animate; must not be {@code null}.
     * @param trainer      the player's {@link Trainer} model; must not be
     *                     {@code null}.
     * @param opponent     the opponent's {@link Trainer} model; must not be
     *                     {@code null}.
     * @param onFinished   the callback to execute once the animation completes;
     *                     must not be {@code null}.
     */
    private void animateAttack(TurnResult.AttackResult attackResult, Trainer trainer,
            Trainer opponent, Runnable onFinished) {
        if (!attackResult.wasAttack()) {
            onFinished.run();
            return;
        }

        if (attackResult.attacker() == trainer) {
            playTrainerAttackAnimation(onFinished);
            return;
        }

        if (attackResult.attacker() == opponent) {
            playOpponentAttackAnimation(onFinished);
            return;
        }

        onFinished.run();
    }

    /**
     * Appends a line describing the given attack result to the turn dialog message
     * and efficiency, prefixed by the given attack index (1 or 2).
     * 
     * @param message    the {@link StringBuilder} to append the attack description
     *                   to; must not be {@code null}.
     * @param efficiency the {@link StringBuilder} to append the attack efficiency
     *                   to; must not be {@code null}.
     * @param index      the index of the attack (1 for the first attack, 2 for the
     *                   second attack).
     * @param result     the attack result to describe; must not be {@code null}.
     */
    private void appendAttackLine(StringBuilder message, StringBuilder efficiency, int index,
            TurnResult.AttackResult result) {
        if (!result.wasAttack()) {
            return;
        }

        if (message.length() > 0) {
            message.append("\n");
            efficiency.append("\n");
        }

        message.append(index)
                .append("- ")
                .append(result.attacker().getCurrentBugemonName())
                .append(" à utilisé l'attaque ")
                .append(result.getAttackName());

        efficiency.append(index).append("- ").append(formatEfficiency(result.efficiency()));
    }

    /**
     * Hides the dialog zone, removing it from the layout flow so that it does
     * not occupy space when empty.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    /**
     * Updates the player-side info panel and sprite to reflect the given
     * Bugemon's current state (name, type, HP).
     *
     * <p>
     * Should be called by the controller at the start of a combat session and
     * after every turn in which the player's active Bugemon may have changed
     * or taken damage.
     * </p>
     *
     * @param trainerBugemon a {@link BugemonDTO} snapshot of the player's
     *                       currently active Bugemon; must not be {@code null}.
     */
    protected void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(trainerBugemon.getSpriteURL()));
    }

    /**
     * Updates the opponent-side info panel and sprite to reflect the given
     * Bugemon's current state (name, type, HP).
     *
     * @param opponentBugemon a {@link BugemonDTO} snapshot of the opponent's
     *                        currently active Bugemon; must not be {@code null}.
     */
    protected void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(opponentBugemon.getSpriteURL()));
    }

    /**
     * Plays a lunge animation on the player's sprite (slide toward the opponent
     * then return), then invokes {@code onFinished} on the JavaFX thread.
     *
     * @param onFinished callback executed once the animation completes; must
     *                   not be {@code null}.
     */
    public void playTrainerAttackAnimation(Runnable onFinished) {
        playLungeAnimation(bugemonTrainerImage, ATTACK_LUNGE_DISTANCE, onFinished);
    }

    /**
     * Plays a lunge animation on the opponent's sprite (slide toward the player
     * then return), then invokes {@code onFinished} on the JavaFX thread.
     *
     * @param onFinished callback executed once the animation completes; must
     *                   not be {@code null}.
     */
    public void playOpponentAttackAnimation(Runnable onFinished) {
        playLungeAnimation(bugemonOpponentImage, -ATTACK_LUNGE_DISTANCE, onFinished);
    }

    /**
     * Plays the attack animation for one side.
     *
     * @param trainerAttacks {@code true} to animate the trainer sprite,
     *                       {@code false} to animate the opponent sprite.
     * @param onFinished     callback executed once the animation completes.
     */
    public void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            playTrainerAttackAnimation(onFinished);
        } else {
            playOpponentAttackAnimation(onFinished);
        }
    }

    /**
     * Slides {@code sprite} by {@code deltaX} pixels over 150 ms then returns
     * it to its original position over another 150 ms.
     *
     * @param sprite     the {@link ImageView} to animate.
     * @param deltaX     the horizontal distance to slide the sprite in pixels.
     * @param onFinished callback executed once the animation completes.
     */
    private void playLungeAnimation(ImageView sprite, double deltaX, Runnable onFinished) {
        TranslateTransition lunge = new TranslateTransition(ATTACK_LUNGE_DURATION, sprite);
        lunge.setByX(deltaX);
        TranslateTransition retreat = new TranslateTransition(ATTACK_LUNGE_DURATION, sprite);
        retreat.setByX(-deltaX);
        SequentialTransition seq = new SequentialTransition(lunge, retreat);
        seq.setOnFinished(e -> onFinished.run());
        seq.play();
    }
}
