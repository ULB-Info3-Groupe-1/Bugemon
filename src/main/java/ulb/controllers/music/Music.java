package ulb.controllers.music;

import java.net.URL;

/**
 * Music
 *
 * @param url
 * @param ambiance
 */
public record Music(URL url, Ambiance ambiance) {
    public static enum Ambiance { COMBAT }
}
