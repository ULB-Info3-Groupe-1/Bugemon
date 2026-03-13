package ulb.controllers.music;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import ulb.controllers.music.exceptions.NoMusicMatchingAmbianceException;

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
    /**
     * Music
     *
     * @param url
     * @param ambiance
     */
    public record Music(URL url, Ambiance ambiance) {
        public static enum Ambiance { COMBAT }
    }

    // NOTE: This is optional because a MediaPlayer's constructor needs a Media
    // instance, but there is no media to play when constructing the MusicPlayer.
    private Optional<MediaPlayer> mediaPlayer;

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
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Plays a random music track according to the given ambiance.
     *
     * @param ambiance the ambiance of the music track to play.
     */
    public void playAmbiance(Music.Ambiance ambiance) {
        List<Music> matchingMusics =
                this.musics.stream().filter(music -> music.ambiance == ambiance).toList();

        if (matchingMusics.isEmpty()) {
            throw new NoMusicMatchingAmbianceException("No music matching ambiance "
                                                       + ambiance.toString());
        }

        Music music =
                matchingMusics.get(ThreadLocalRandom.current().nextInt(matchingMusics.size()));

        this.playMusic(music);
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
