package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.Music;
import ulb.models.music.SoundEffect;
import ulb.repositories.MusicRepository;

/**
 * Service that manages background music and sound-effect playback using JavaFX {@link javafx.scene.media.MediaPlayer}.
 *
 * <p>
 * Background music loops indefinitely and is automatically paused while a sound effect is playing, then resumed once
 * all active sound effects have finished. Changing the ambiance stops the current track and starts a randomly chosen
 * replacement.
 */
public class MusicService {

    private static final Logger LOG = LoggerFactory.getLogger(MusicService.class);

    private final MusicRepository musicRepository;
    private final List<MediaPlayer> activeSoundEffects;

    private MediaPlayer mediaPlayer;
    private BackgroundAmbiance currentAmbiance;
    private int sfxPlayingCount = 0;
    private boolean wasMusicPlaying = false;
    private double volume = Configuration.Ui.DEFAULT_VOLUME;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
        this.activeSoundEffects = new ArrayList<>();
    }

    /**
     * Returns the current master volume, in the range {@code [0.0, 1.0]}, applied to both background music and sound
     * effects.
     *
     * @return the current master volume
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Sets the master volume applied to background music and sound effects. The value is clamped to {@code [0.0, 1.0]}
     * and applied immediately to the currently playing track and every active sound effect.
     *
     * @param volume
     *            the desired volume; values outside {@code [0.0, 1.0]} are clamped
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setVolume(this.volume);
        }
        for (MediaPlayer sfxPlayer : this.activeSoundEffects) {
            sfxPlayer.setVolume(this.volume);
        }
    }

    /**
     * Starts looping background music appropriate for {@code ambiance}. If the requested ambiance is already playing,
     * this call is a no-op. Selects a random track from all tracks matching the ambiance.
     *
     * @param ambiance
     *            the desired background ambiance
     */
    public void playBackground(BackgroundAmbiance ambiance) {
        if (this.currentAmbiance == ambiance && this.mediaPlayer != null) {
            LOG.debug("Music already playing for ambiance {}, keeping current track", ambiance);
            return;
        }

        this.stopMusic();

        List<Music> tracks = this.musicRepository.findByAmbiance(ambiance);
        if (tracks.isEmpty()) {
            LOG.error("Error playing music matching ambiance {}: no match", ambiance);
            return;
        }

        Music music = tracks.get(ThreadLocalRandom.current().nextInt(tracks.size()));
        try {
            Media track = new Media(music.url().toExternalForm());
            this.mediaPlayer = new MediaPlayer(track);
            this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            this.mediaPlayer.setVolume(this.volume);

            if (this.sfxPlayingCount == 0) {
                this.mediaPlayer.play();
            } else {
                this.wasMusicPlaying = true;
            }

            this.currentAmbiance = ambiance;
            LOG.debug("Now playing music: {}", ambiance);
        } catch (Exception e) {
            LOG.error("Error playing background music: {}", e.getMessage());
        }
    }

    /**
     * Plays a one-shot sound effect for {@code effect}. Background music is paused when the first sound effect starts
     * and resumed when the last one ends. Multiple sound effects can play concurrently.
     *
     * @param effect
     *            the sound effect to play
     */
    public void playSoundEffect(SoundEffect effect) {
        List<Music> tracks = this.musicRepository.findByEffect(effect);
        if (tracks.isEmpty()) {
            LOG.error("Error playing sound effect {}: no match", effect);
            return;
        }

        Music music = tracks.get(ThreadLocalRandom.current().nextInt(tracks.size()));
        try {
            Media track = new Media(music.url().toExternalForm());
            MediaPlayer sfxPlayer = new MediaPlayer(track);
            sfxPlayer.setVolume(this.volume);
            this.activeSoundEffects.add(sfxPlayer);

            if (this.sfxPlayingCount == 0 && this.mediaPlayer != null
                    && this.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                this.wasMusicPlaying = true;
                this.mediaPlayer.pause();
                LOG.debug("Background music paused for sound effect");
            }
            this.sfxPlayingCount++;

            sfxPlayer.setCycleCount(1);
            sfxPlayer.setOnEndOfMedia(() -> {
                sfxPlayer.stop();
                sfxPlayer.dispose();
                this.activeSoundEffects.remove(sfxPlayer);
                this.sfxPlayingCount--;

                if (this.sfxPlayingCount == 0 && this.wasMusicPlaying && this.mediaPlayer != null) {
                    this.mediaPlayer.play();
                    this.wasMusicPlaying = false;
                    LOG.debug("Background music resumed");
                }
            });

            sfxPlayer.play();
        } catch (Exception e) {
            LOG.error("Error playing sound effect: {}", e.getMessage());
        }
    }

    /** Stops and disposes the currently playing background track. Does nothing if no track is active. */
    public void stopMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.dispose();
            this.mediaPlayer = null;
        }
        this.currentAmbiance = null;
        this.wasMusicPlaying = false;
    }
}
