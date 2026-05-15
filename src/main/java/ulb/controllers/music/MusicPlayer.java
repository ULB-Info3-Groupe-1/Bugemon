package ulb.controllers.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.MusicRepository;

/**
 * Manages music playback; holds a list of registered tracks and plays them via JavaFX {@link MediaPlayer}.
 */
public class MusicPlayer {
    private static final Logger LOG = LoggerFactory.getLogger(MusicPlayer.class);

    private final MusicRepository repository;
    private final List<MediaPlayer> activeSoundEffects;

    // NOTE: This is optional because a MediaPlayer's constructor needs a Media
    // instance, but there is no media to play when constructing the MusicPlayer.
    private Optional<MediaPlayer> mediaPlayer;
    private Optional<Music> currentMusic;

    public MusicPlayer() {
        this.repository = new MusicRepository();
        this.activeSoundEffects = new ArrayList<>();
        this.mediaPlayer = Optional.empty();
        this.currentMusic = Optional.empty();
    }

    /**
     * Loads all music tracks from the repository.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void loadAllMusics() throws IOException {
        this.repository.loadAllResources();
    }

    /**
     * Plays the given music. If the given music is already playing, does nothing.
     *
     * @param music
     *            the music
     */
    private void playMusic(Music music) {
        if (this.currentMusic.isPresent() && this.currentMusic.get().ambiance() == music.ambiance()
                && this.mediaPlayer.isPresent()) {
            LOG.debug("Music already playing for ambiance {}, keeping current track", music.ambiance());
            return;
        }

        this.stopMusic();

        try {
            Media track = new Media(music.url().toExternalForm());

            // instanciate mediaPlayer + play music
            this.mediaPlayer = Optional.of(new MediaPlayer(track));
            this.mediaPlayer.ifPresent(player -> {
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.play();
                this.currentMusic = Optional.of(music);
                LOG.debug("Now playing music: {}", music.ambiance());
            });
        } catch (Exception e) {
            LOG.error("Error playing music: {}", e.getMessage());
        }
    }

    /**
     * Plays the given music as a sound effect.
     *
     * @param music
     *            the music to play as a sound effect
     */
    public void playSoundEffect(Music music) {
        try {
            Media track = new Media(music.url().toExternalForm());
            MediaPlayer sfxPlayer = new MediaPlayer(track);
            this.activeSoundEffects.add(sfxPlayer);
            sfxPlayer.setCycleCount(1);
            sfxPlayer.setOnEndOfMedia(() -> {
                sfxPlayer.stop();
                sfxPlayer.dispose();
                this.activeSoundEffects.remove(sfxPlayer);
            });
            sfxPlayer.play();
        } catch (Exception e) {
            LOG.error("Error playing sound effect: {}", e.getMessage());
        }
    }

    /**
     * Plays a random music track according to the given ambiance.
     *
     * @param ambiance
     *            the ambiance of the music track to play.
     */
    public void playAmbiance(Ambiance ambiance, boolean isSoundEffect) {
        List<Music> matchingMusics = this.repository.findByAmbiance(ambiance);

        if (matchingMusics.isEmpty()) {
            LOG.error("Error playing music  matching ambiance {}: no match", ambiance);
            return;
        }

        Music music = matchingMusics.get(ThreadLocalRandom.current().nextInt(matchingMusics.size()));

        if (isSoundEffect) {
            this.stopMusic();
            this.playSoundEffect(music);
        } else {
            this.playMusic(music);
        }
    }

    /**
     * Stops the currently playing music track if there is one by calling the stop method on the MediaPlayer instance.
     */
    public void stopMusic() {
        this.mediaPlayer.ifPresent(player -> {
            player.stop();
            player.dispose();
        });
        this.mediaPlayer = Optional.empty();
        this.currentMusic = Optional.empty();
    }
}
