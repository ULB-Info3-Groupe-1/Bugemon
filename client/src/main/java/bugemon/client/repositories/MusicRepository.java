package bugemon.client.repositories;

import java.util.List;

import bugemon.common.models.music.BackgroundAmbiance;
import bugemon.common.models.music.Music;
import bugemon.common.models.music.SoundEffect;

/**
 * Repository that supplies {@link Music} tracks grouped by their intended usage context.
 */
public interface MusicRepository {

    /**
     * Returns all background tracks associated with the given ambiance category.
     *
     * @param ambiance
     *            the scene type (e.g. {@link BackgroundAmbiance#COMBAT}, {@link BackgroundAmbiance#MENU})
     * @return list of matching tracks, possibly empty
     */
    List<Music> findByAmbiance(BackgroundAmbiance ambiance);

    /**
     * Returns all one-shot sound effects associated with the given effect type.
     *
     * @param effect
     *            the event type (e.g. {@link SoundEffect#VICTORY}, {@link SoundEffect#DEFEAT})
     * @return list of matching tracks, possibly empty
     */
    List<Music> findByEffect(SoundEffect effect);

}
