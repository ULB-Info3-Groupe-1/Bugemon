package ulb.repositories;

import java.util.List;

import ulb.models.music.Ambiance;
import ulb.models.music.Music;

public interface MusicRepository {

    /**
     * Returns all musics with the given ambiance
     *
     * @param ambiance
     *            the ambiance of the musics to return
     * @return a list of musics matching the ambiance
     */
    List<Music> findByAmbiance(Ambiance ambiance);

}
