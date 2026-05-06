package ulb.views.combat;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/** Manages attack and death animations for the two Bugemon sprites in a combat screen. */
public class CombatAnimationView {
    private static final double ATTACK_LUNGE_DISTANCE = 100;
    private static final Duration ATTACK_LUNGE_DURATION = Duration.millis(150);

    private static final Duration DEATH_BUGEMON_DURATION = Duration.millis(500);
    private static final double DEFAULT_OPACITY = 1.0;
    private static final double SHAKE_DISTANCE_X = 5.0;
    private static final int HIT_EFFECT_CYCLE_COUNT = 4;
    private static final double FLASH_MIN_OPACITY = 0.3;
    private static final double DEATH_SCALE_FACTOR = 0.2;
    private static final double FLOAT_UP_DISTANCE_Y = -30.0;
    private static final double DEFAULT_SCALE = 1.0;
    private static final double DEFAULT_TRANSLATION = 0.0;

    private final ImageView trainerSprite;
    private final ImageView opponentSprite;

    /**
     * Creates a new CombatAnimationView.
     *
     * @param trainerSprite
     *            the trainer bugemon sprite
     * @param opponentSprite
     *            the opponent bugemon sprite
     */
    public CombatAnimationView(ImageView trainerSprite, ImageView opponentSprite) {
        this.trainerSprite = trainerSprite;
        this.opponentSprite = opponentSprite;
    }

    /**
     * Plays the trainer attack animation (lunge forward then back).
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playTrainerAttackAnimation(Runnable onFinished) {
        this.playLungeAnimation(this.trainerSprite, ATTACK_LUNGE_DISTANCE, onFinished);
    }

    /**
     * Plays the opponent attack animation (lunge forward then back).
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playOpponentAttackAnimation(Runnable onFinished) {
        this.playLungeAnimation(this.opponentSprite, -ATTACK_LUNGE_DISTANCE, onFinished);
    }

    private void playLungeAnimation(ImageView sprite, double deltaX, Runnable onFinished) {
        TranslateTransition lunge = new TranslateTransition(ATTACK_LUNGE_DURATION, sprite);
        lunge.setByX(deltaX);
        TranslateTransition retreat = new TranslateTransition(ATTACK_LUNGE_DURATION, sprite);
        retreat.setByX(-deltaX);

        SequentialTransition seq = new SequentialTransition(lunge, retreat);
        seq.setOnFinished(e -> onFinished.run());
        seq.play();
    }

    /**
     * Plays the death animation for the trainer's active Bugemon.
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playDeathAnimationForTrainer(Runnable onFinished) {
        this.playDeathAnimation(this.trainerSprite, onFinished);
    }

    /**
     * Plays the death animation for the opponent's active Bugemon.
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playDeathAnimationForOpponent(Runnable onFinished) {
        this.playDeathAnimation(this.opponentSprite, onFinished);
    }

    private void playDeathAnimation(ImageView sprite, Runnable onFinished) {
        sprite.setOpacity(DEFAULT_OPACITY);

        TranslateTransition shake = new TranslateTransition(DEATH_BUGEMON_DURATION, sprite);
        shake.setByX(SHAKE_DISTANCE_X);
        shake.setAutoReverse(true);
        shake.setCycleCount(HIT_EFFECT_CYCLE_COUNT);

        FadeTransition flash = new FadeTransition(DEATH_BUGEMON_DURATION, sprite);
        flash.setFromValue(DEFAULT_OPACITY);
        flash.setToValue(FLASH_MIN_OPACITY);
        flash.setAutoReverse(true);
        flash.setCycleCount(HIT_EFFECT_CYCLE_COUNT);

        ScaleTransition shrink = new ScaleTransition(DEATH_BUGEMON_DURATION, sprite);
        shrink.setToX(DEATH_SCALE_FACTOR);
        shrink.setToY(DEATH_SCALE_FACTOR);

        FadeTransition fade = new FadeTransition(DEATH_BUGEMON_DURATION, sprite);
        fade.setToValue(DEFAULT_TRANSLATION);

        TranslateTransition floatUp = new TranslateTransition(DEATH_BUGEMON_DURATION, sprite);
        floatUp.setByY(FLOAT_UP_DISTANCE_Y);

        ParallelTransition death = new ParallelTransition(shrink, fade, floatUp);
        ParallelTransition hitEffect = new ParallelTransition(shake, flash);

        SequentialTransition sequence = new SequentialTransition(hitEffect, death);
        sequence.setOnFinished(e -> onFinished.run());
        sequence.play();
    }

    /**
     * Resets {@code sprite} to its default visible, full-size, untranslated state.
     *
     * @param sprite
     *            the sprite
     */
    public void makeBugemonReappear(ImageView sprite) {
        sprite.setOpacity(DEFAULT_OPACITY);
        sprite.setScaleX(DEFAULT_SCALE);
        sprite.setScaleY(DEFAULT_SCALE);
        sprite.setTranslateX(DEFAULT_TRANSLATION);
        sprite.setTranslateY(DEFAULT_TRANSLATION);
    }
}
