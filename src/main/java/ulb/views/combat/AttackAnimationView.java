package ulb.views.combat;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Class responsible for managing attack animations in combat views.
 */
public class AttackAnimationView {
    // Attributes
    private static final double ATTACK_LUNGE_DISTANCE = 100;
    private static final Duration ATTACK_LUNGE_DURATION = Duration.millis(150);

    private final ImageView trainerSprite;
    private final ImageView opponentSprite;

    // Constructor

    /**
     * Creates an animation view bound to the two combat sprites.
     *
     * @param trainerSprite  trainer sprite image view.
     * @param opponentSprite opponent sprite image view.
     */
    public AttackAnimationView(ImageView trainerSprite, ImageView opponentSprite) {
        this.trainerSprite = trainerSprite;
        this.opponentSprite = opponentSprite;
    }

    // Methods

    /**
     * Plays the trainer attack animation.
     *
     * @param onFinished callback executed when the animation ends.
     */
    public void playTrainerAttackAnimation(Runnable onFinished) {
        playLungeAnimation(trainerSprite, ATTACK_LUNGE_DISTANCE, onFinished);
    }

    /**
     * Plays the opponent attack animation.
     *
     * @param onFinished callback executed when the animation ends.
     */
    public void playOpponentAttackAnimation(Runnable onFinished) {
        playLungeAnimation(opponentSprite, -ATTACK_LUNGE_DISTANCE, onFinished);
    }

    /**
     * Slides a sprite forward then back.
     *
     * @param sprite     the sprite to animate.
     * @param deltaX     the distance to slide the sprite (positive or negative
     *                   depending on direction).
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