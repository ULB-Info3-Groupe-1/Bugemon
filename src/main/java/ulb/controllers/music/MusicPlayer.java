package ulb.controllers.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * MusicPlayer
 *
 * Controller responsible for managing music playback within the application.
 * Handles loading, playing, stopping, and switching between music tracks.
 *
 * <p>
 * The controller maintains a list of available music tracks, each represented
 * by the inner {@code Music} class which encapsulates the song's link and name.
 * The controller uses JavaFX's {@link MediaPlayer} for audio playback.
 * </p>
 *
 * @see MediaPlayer
 */
public class MusicPlayer {
    // NOTE: This is optional because a MediaPlayer's constructor needs a Media
    // instance, but there is no media to play when constructing the MusicPlayer.
    private Optional<MediaPlayer> mediaPlayer;
    private Optional<MediaPlayer> soundEffectPlayer;

    private List<Music> musics;

    /**
     * MusicPlayer constructor
     */
    public MusicPlayer() {
        this.musics = new ArrayList<>();
        this.mediaPlayer = Optional.empty();
    }

    /**
     * Adds the given music to the available music tracks.
     */
    public void addMusic(Music music) {
        this.musics.add(music);
    }

    /**
     * Plays the given music.
     *
     * @param music the music
     */
    private void playMusic(Music music) {
        this.stopMusic();

        try {
            Media track = new Media(music.url().toExternalForm());

            // instanciate mediaPlayer + play music
            this.mediaPlayer = Optional.of(new MediaPlayer(track));
            this.mediaPlayer.ifPresent(player -> {
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.play();
            });
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Plays the given music as a sound effect.
     *
     * @param music the music to play as a sound effect
     */
    public void playSoundEffect(Music music) {
        try {
            Media track = new Media(music.url().toExternalForm());
            this.soundEffectPlayer = Optional.of(new MediaPlayer(track));
            this.soundEffectPlayer.ifPresent(player -> {
                player.setCycleCount(1);
                player.play();
            });
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    /**
     * Plays a random music track according to the given ambiance.
     *
     * @param ambiance the ambiance of the music track to play.
     */
    public void playAmbiance(Ambiance ambiance, boolean isSoundEffect) {
        List<Music> matchingMusics =
                this.musics.stream().filter(music -> music.ambiance() == ambiance).toList();

        if (matchingMusics.isEmpty()) {
            System.err.println("Error playing music matching ambiance " + ambiance.toString()
                               + ": no match");
            return;
        }

        Music music =
                matchingMusics.get(ThreadLocalRandom.current().nextInt(matchingMusics.size()));

        if (isSoundEffect) {
            this.playSoundEffect(music);
        } else {
            this.playMusic(music);
        }
    }

    /**
     * Stops the currently playing music track if there is one by calling the stop
     * method on the MediaPlayer instance.
     */
    public void stopMusic() {
        this.mediaPlayer.ifPresent(player -> player.stop());
        this.mediaPlayer = Optional.empty();
    }
}
