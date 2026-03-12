/**
 * File name : MusicController.java
 * Description : Class representing a music controller.
 *
 * @author Liefferinckx Romain
 * @date 12 march. 2026
 * @version 1.0
 */

package ulb.controllers.music;

import ulb.models.music.Music;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * MusicController
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
public class MusicController {

    private static MusicController instance;

    private MediaPlayer mediaPlayer;
    private List<Music> musicList;

    private final static String PATH = "/musics/";
    private final static String COMBAT_SONG_1 = "combatSong1.wav";

    /**
     * Private constructor (Singleton)
     */
    private MusicController() {
        musicList = new ArrayList<>();
        addMusic(PATH + COMBAT_SONG_1);
    }

    /**
     * Returns the unique instance of MusicController
     */
    public static MusicController getInstance() {
        if (instance == null) {
            instance = new MusicController();
        }
        return instance;
    }

    // Methods

    /**
     * Adds a music track to the music list by creating a new Music object with the
     * provided resource path and adding it to the list.
     * 
     * @param resourcePath (String) the path to the music file resource.
     */
    private void addMusic(String resourcePath) {
        String url = getClass().getResource(resourcePath).toExternalForm();
        Music music = new Music(url);
        musicList.add(music);
    }

    /**
     * Plays the music track with the specified name by searching through the music
     * list.
     * 
     * @param songName (String) the name of the music track to play.
     */
    private void playMusic(String songName) {
        for (Music music : musicList) {
            if (music.getSongName().equals(songName)) {
                try {
                    System.out.println("Playing music: " + music.getSongName());
                    Media hit = new Media(music.getSongLink());
                    mediaPlayer = new MediaPlayer(hit);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.play();
                } catch (Exception e) {
                    System.out.println("Error playing music: " + e.getMessage());
                }
                return;
            }
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
