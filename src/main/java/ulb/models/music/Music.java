package ulb.models.music;

import java.net.URL;

/**
 * Holds the resource URL of a looping background music track.
 *
 * @param url
 *            the URL pointing to the audio resource
 */
public record Music(URL url) {
}
