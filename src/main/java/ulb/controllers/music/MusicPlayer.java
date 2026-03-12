package ulb.controllers.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * MusicPlayer
 *
 * A singleton controller responsible for managing music playback within the
 * application.
 * Handles loading, playing, stopping, and switching between music tracks.
 *
 * <p>
 * The controller maintains a list of available music tracks, each represented
 * by the inner {@code Music} class which encapsulates the song's link and name.
 * Music files are loaded from the application's resources and can be played by
 * their name. The controller uses JavaFX's {@link MediaPlayer} for audio
 * playback.
 * </p>
 *
 * @see MediaPlayer
 */
public class MusicPlayer {

    public record Music(String path, Ambiance ambiance) {

        static enum Ambiance {
            COMBAT
        }

        String getName() {
            // TODO: impl
            return "some name";
        }

        String getPath() {
            // TODO: impl
            return "some path";
        }

    };

    private static MusicPlayer instance;

    private MediaPlayer mediaPlayer;
    private List<Music> musics;

    /**
     * Private constructor (Singleton)
     */
    private MusicPlayer() {
        musics = new ArrayList<>();
    }

    /**
     * Returns the unique instance of MusicController
     */
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    private void addMusic(Music music) {
        this.musics.add(music);
    }

    /**
     * Plays the music track with the specified name by searching through the
     * music
     * list.
     *
     * @param songName (String) the name of the music track to play.
     */
    private void playMusic(Music.Ambiance ambiance) {
        List<Music> musics = this.musics.stream()
                .filter(music -> music.ambiance == ambiance).toList();

        if (musics.isEmpty()) {
            // TODO: might wanna throw an exception here
            return;
        }

        Music music = musics.get(ThreadLocalRandom.current().nextInt(musics.size()));

        try {
            System.out.println("Playing music: " + music.getName());
            Media hit = new Media(music.getPath());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Stops the currently playing music track if there is one by calling the stop
     * method on the MediaPlayer instance.
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Switches the currently playing music track to the one specified by songName
     * by
     * first stopping any currently playing track and then calling playMusic with
     * the
     * new song name.
     *
     * @param songName (String) the name of the music track to switch to.
     */
    public void switchMusic(String songName) {
        stopMusic();
        playMusic(songName);
    }
}
