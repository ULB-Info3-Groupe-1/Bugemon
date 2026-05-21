package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.Music;
import ulb.models.music.SoundEffect;
import ulb.repositories.MusicRepository;

public class MusicService {

    private static final Logger LOG = LoggerFactory.getLogger(MusicService.class);

    private final MusicRepository musicRepository;
    private final List<MediaPlayer> activeSoundEffects;

    private MediaPlayer mediaPlayer;
    private BackgroundAmbiance currentAmbiance;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
        this.activeSoundEffects = new ArrayList<>();
    }

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
            this.mediaPlayer.play();
            this.currentAmbiance = ambiance;
            LOG.debug("Now playing music: {}", ambiance);
        } catch (Exception e) {
            LOG.error("Error playing background music: {}", e.getMessage());
        }
    }

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

    public void stopMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.dispose();
            this.mediaPlayer = null;
        }
        this.currentAmbiance = null;
    }
}
