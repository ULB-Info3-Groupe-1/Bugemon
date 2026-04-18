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
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;

/**
 * Loads {@link Music} files from classpath resources (works both on the filesystem and inside a JAR).
 */
public class MusicLoader {
    private static final Logger LOG = LoggerFactory.getLogger(MusicLoader.class);

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

        if (!"jar".equals(uri.getScheme())) {
            return this.collectMusic(Paths.get(uri), ambiance);
        }

        try {
            FileSystem fs = FileSystems.getFileSystem(uri);
            return this.collectMusic(fs.getPath(resourceDir), ambiance);
        } catch (java.nio.file.FileSystemNotFoundException e) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, java.util.Map.of())) {
                return this.collectMusic(fs.getPath(resourceDir), ambiance);
            }
        }
    }

    private List<Music> collectMusic(Path dir, Ambiance ambiance) throws IOException {
        return this.listFiles(dir).stream().map(path -> this.loadMusic(path, ambiance)).flatMap(Optional::stream)
                .toList();
    }

    private URI getResourceURI(String resourceDir) {
        URL url = MusicLoader.class.getResource(resourceDir);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceDir);
        }

        try {
            return url.toURI();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid resource URI for path: " + resourceDir, e);
        }
    }

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
        this.loadFromDirectory(Configuration.Music.MUSIC_PATH_COMBAT, Ambiance.COMBAT).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(Configuration.Music.MUSIC_PATH_MENU, Ambiance.MENU).forEach(musicPlayer::addMusic);
        this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_VICTORY, Ambiance.VICTORY)
                .forEach(musicPlayer::addMusic);
        this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_DEFEAT, Ambiance.DEFEAT)
                .forEach(musicPlayer::addMusic);
    }

    private Optional<Music> loadMusic(Path path, Ambiance ambiance) {
        try {
            return Optional.of(new Music(path.toUri().toURL(), ambiance));
        } catch (Exception e) {
            LOG.error("Error loading song: {}", path);
            return Optional.empty();
        }
    }
}
