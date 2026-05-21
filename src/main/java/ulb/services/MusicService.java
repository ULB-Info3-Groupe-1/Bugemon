package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.music.Ambiance;
import ulb.models.music.Music;
import ulb.repositories.MusicRepository;

public class MusicService {

    private static final Logger LOG = LoggerFactory.getLogger(MusicService.class);

    private final MusicRepository musicRepository;
    private final List<MediaPlayer> activeSoundEffects;

    private MediaPlayer mediaPlayer;
    private Music currentMusic;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
        this.activeSoundEffects = new ArrayList<>();
    }

    private void playMusic(Music music) {
        if (this.currentMusic != null && this.mediaPlayer != null && this.currentMusic.ambiance() == music.ambiance()) {
            LOG.debug("Music already playing for ambiance {}, keeping current track", music.ambiance());
            return;
        }

        this.stopMusic();

        try {
            Media track = new Media(music.url().toExternalForm());
            this.mediaPlayer = new MediaPlayer(track);

            this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            this.mediaPlayer.play();

            this.currentMusic = music;
            LOG.debug("Now playing music: {}", music.ambiance());
        } catch (Exception e) {
            LOG.error("Error playing music: {}", e.getMessage());
        }
    }

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

    public void playAmbiance(Ambiance ambiance, boolean isSoundEffect) {
        List<Music> matchingMusics = this.musicRepository.findByAmbiance(ambiance);

        if (matchingMusics.isEmpty()) {
            LOG.error("Error playing music matching ambiance {}: no match", ambiance);
            return;
        }

        Music music = matchingMusics.get(ThreadLocalRandom.current().nextInt(matchingMusics.size()));

        if (isSoundEffect) {
            this.playSoundEffect(music);
        } else {
            this.playMusic(music);
        }
    }

    public void stopMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.dispose();
            this.mediaPlayer = null;
        }
        this.currentMusic = null;
    }
}
