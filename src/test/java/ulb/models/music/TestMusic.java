/**
 * File name : TestMusic.java
 * Description : Test class for the Music class.
 *
 * @author Liefferinckx Romain
 * @date 12 march. 2026
 * @version 1.0
 */

package ulb.models.music;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestMusic {
    @Test
    public void testGetName() {
        Music music = new Music("/musics/combatSong1.wav");
        assertEquals("combatSong1", music.getSongName());
    }
}
