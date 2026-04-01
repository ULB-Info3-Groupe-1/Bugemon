package ulb.controllers.music;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * MusicLoarder
 */
public class MusicLoader {
    private static final Logger LOGGER = Logger.getLogger(MusicLoader.class.getName());
    private static final String MUSIC_DIR = "/musics/";
    private static final String SOUND_EFFECTS_DIR = "/sound_effects/";

    /**
     * Loads all music files from the given resource directory and assigns them the given ambiance.
     *
     * @param resourceDir
     *            path to the resource directory (inside JAR or filesystem)
     * @param ambiance
     *            ambiance to assign to each loaded music
     * @return list of loaded Music objects
     * @throws IOException
     *             if the directory cannot be accessed
     */
    public List<Music> loadFromDirectory(String resourceDir, Ambiance ambiance) throws IOException {
        URI uri = this.getResourceURI(resourceDir);
        Path dir = this.resolveDirectory(uri, resourceDir);

        return this.listFiles(dir).stream().map(path -> this.loadMusic(path, ambiance)).flatMap(Optional::stream)
                .toList();
    }

    /**
     * Resolves the URI of the given resource directory.
     *
     * @param resourceDir
     *            path to the resource
     * @return URI of the resource
     * @throws IllegalArgumentException
     *             if the resource does not exist
     */
    private URI getResourceURI(String resourceDir) {
        URL url = getClass().getResource(resourceDir);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceDir);
        }

        try {
            return url.toURI();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid resource URI for path: " + resourceDir, e);
        }
    }

    /**
     * Converts a URI to a Path, handling both filesystem and JAR schemes.
     *
     * @param uri
     *            the URI to resolve
     * @param resourceDir
     *            resource path inside the URI
     * @return Path representing the directory
     * @throws IOException
     *             if the directory cannot be accessed
     */
    private Path resolveDirectory(URI uri, String resourceDir) throws IOException {
        if ("jar".equals(uri.getScheme())) {
            try {
                return FileSystems.getFileSystem(uri).getPath(resourceDir);
            } catch (java.nio.file.FileSystemNotFoundException e) {
                try (FileSystem fs = FileSystems.newFileSystem(uri, java.util.Map.of())) {
                    return fs.getPath(resourceDir);
                }
            }
        } else {
            return Paths.get(uri);
        }
    }

    /**
     * Lists all regular files in the given directory.
     *
     * @param dir
     *            directory to list
     * @return list of file paths
     * @throws IOException
     *             if listing fails
     */
    private List<Path> listFiles(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile).toList();
        }
    }

    /**
     * Loads all game music and sound effects and registers them with the provided player.
     *
     * @param musicPlayer
     *            the music player to register music with
     * @throws IOException
     *             if any resource directory cannot be accessed
     */
    public void loadAllResources(MusicPlayer musicPlayer) throws IOException {
        this.loadFromDirectory(MUSIC_DIR + "combat", Ambiance.COMBAT).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(MUSIC_DIR + "menu", Ambiance.MENU).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(MUSIC_DIR + "create_team", Ambiance.CREATE_TEAM).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(SOUND_EFFECTS_DIR + "victory", Ambiance.VICTORY).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(SOUND_EFFECTS_DIR + "defeat", Ambiance.DEFEAT).forEach(musicPlayer::addMusic);
    }

    /**
     * Loads a single music file from a path and assigns the given ambiance.
     *
     * @param path
     *            path to the music file
     * @param ambiance
     *            ambiance to assign
     * @return optional containing the Music object if loaded successfully
     */
    private Optional<Music> loadMusic(Path path, Ambiance ambiance) {
        try {
            return Optional.of(new Music(path.toUri().toURL(), ambiance));
        } catch (Exception e) {
            LOGGER.severe("error loading song: " + path);
            return Optional.empty();
        }
    }
}
