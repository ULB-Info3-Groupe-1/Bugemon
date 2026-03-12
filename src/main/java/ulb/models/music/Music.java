/**
 * File name : Music.java
 * Description : Class representing a music track.
 *
 * @author Liefferinckx Romain
 * @date 12 march. 2026
 * @version 1.0
 */

package ulb.models.music;

/**
 * Music
 * 
 * Class representing a music track, encapsulating its resource link and
 * name.
 * 
 * <p>
 * The {@code Music} class provides a constructor that takes a resource path,
 * extracts the song name from the file name, and stores both the link and name
 * for later retrieval. The song name is derived by removing the file extension
 * from the last segment of the resource path.
 * </p>
 */
public class Music {
    private String songLink;
    private String songName;

    // Constructor

    public Music(String songLink) {
        this.songLink = songLink;
        this.songName = setSongName();
    }

    // Methods

    /**
     * Extracts the song name from the song link by taking the last segment of the
     * path
     * and removing the file extension.
     * 
     * @return (String) the extracted song name
     */
    private String setSongName() {
        String[] parts = songLink.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    // Getters

    /**
     * Returns the resource link of the music track.
     * 
     * @return (String) the song link
     */
    public String getSongLink() {
        return songLink;
    }

    /**
     * Returns the name of the music track.
     * 
     * @return (String) the song name
     */
    public String getSongName() {
        return songName;
    }
}