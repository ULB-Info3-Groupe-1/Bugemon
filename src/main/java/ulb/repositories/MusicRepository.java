package ulb.repositories;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.Music;

/**
 * Loads {@link Music} files from classpath resources (works both on the filesystem and inside a JAR).
 */
public class MusicRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MusicRepository.class);

    private final List<Music> musics = new ArrayList<>();

    /**
     * Returns all musics with the given ambiance
     *
     * @param ambiance
     *            the ambiance of the musics to return
     * @return a list of musics matching the ambiance
     */
    public List<Music> findByAmbiance(Ambiance ambiance) {
        return this.musics.stream().filter(music -> music.ambiance() == ambiance).toList();
    }

    /**
     * Loads all music resources from the classpath.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void loadAllResources() throws IOException {
        this.musics.addAll(this.loadFromDirectory(Configuration.Music.MUSIC_PATH_COMBAT, Ambiance.COMBAT));
        this.musics.addAll(this.loadFromDirectory(Configuration.Music.MUSIC_PATH_MENU, Ambiance.MENU));
        this.musics.addAll(this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_VICTORY, Ambiance.VICTORY));
        this.musics.addAll(this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_DEFEAT, Ambiance.DEFEAT));
        LOG.debug("All musics have been loaded successfully into the repository");
    }

    private List<Music> loadFromDirectory(String resourceDir, Ambiance ambiance) throws IOException {
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
        URL url = MusicRepository.class.getResource(resourceDir);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceDir);
        }

        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid resource URI for path: " + resourceDir, e);
        }
    }

    private List<Path> listFiles(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile).toList();
        }
    }

    private Optional<Music> loadMusic(Path path, Ambiance ambiance) {
        try {
            return Optional.of(new Music(path.toUri().toURL(), ambiance));
        } catch (MalformedURLException e) {
            LOG.error("Error loading song: {}", path);
            return Optional.empty();
        }
    }
}
