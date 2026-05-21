package ulb.repositories;

import java.util.List;

import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.Music;
import ulb.models.music.SoundEffect;

public interface MusicRepository {

    List<Music> findByAmbiance(BackgroundAmbiance ambiance);

    List<Music> findByEffect(SoundEffect effect);

}
