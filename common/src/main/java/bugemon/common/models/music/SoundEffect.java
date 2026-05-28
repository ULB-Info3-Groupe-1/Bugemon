package bugemon.common.models.music;

/**
 * One-shot audio cues played in response to game events.
 *
 * <ul>
 * <li>{@link #VICTORY} — played when the player wins a combat.</li>
 * <li>{@link #DEFEAT} — played when the player loses a combat.</li>
 * </ul>
 */
public enum SoundEffect {
    VICTORY,
    DEFEAT
}
