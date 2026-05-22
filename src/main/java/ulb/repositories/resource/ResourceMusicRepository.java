package ulb.repositories.resource;

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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.Music;
import ulb.models.music.SoundEffect;
import ulb.repositories.MusicRepository;

/**
 * Loads {@link Music} files from classpath resources (works both on the filesystem and inside a JAR).
 */
public class ResourceMusicRepository implements MusicRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceMusicRepository.class);

    private final Map<BackgroundAmbiance, List<Music>> backgroundTracks = new EnumMap<>(BackgroundAmbiance.class);
    private final Map<SoundEffect, List<Music>> soundEffectTracks = new EnumMap<>(SoundEffect.class);

    public ResourceMusicRepository() {
        this.loadAllResources();
    }

    @Override
    public List<Music> findByAmbiance(BackgroundAmbiance ambiance) {
        return this.backgroundTracks.getOrDefault(ambiance, List.of());
    }

    @Override
    public List<Music> findByEffect(SoundEffect effect) {
        return this.soundEffectTracks.getOrDefault(effect, List.of());
    }

    private void loadAllResources() {
        try {
            this.backgroundTracks.put(BackgroundAmbiance.COMBAT,
                    this.loadFromDirectory(Configuration.Music.MUSIC_PATH_COMBAT));
            this.backgroundTracks.put(BackgroundAmbiance.MENU,
                    this.loadFromDirectory(Configuration.Music.MUSIC_PATH_MENU));
            this.soundEffectTracks.put(SoundEffect.VICTORY,
                    this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_VICTORY));
            this.soundEffectTracks.put(SoundEffect.DEFEAT,
                    this.loadFromDirectory(Configuration.Music.SOUND_EFFECTS_PATH_DEFEAT));
        } catch (IOException e) {
            LOG.error("Error loading musics: {}", e.getMessage());
        }
        LOG.debug("All musics have been loaded successfully into the repository");
    }

    /**
     * Loads all {@link Music} entries from the given classpath directory, handling both regular-filesystem and
     * JAR-filesystem cases.
     *
     * @param resourceDir
     *            the classpath-relative directory path (e.g. {@code /music/combat/})
     * @return list of loaded {@link Music} instances; empty if the directory contains no files
     * @throws IOException
     *             if the directory cannot be walked
     */
    private List<Music> loadFromDirectory(String resourceDir) throws IOException {
        URI uri = this.getResourceURI(resourceDir);

        if (!"jar".equals(uri.getScheme())) {
            return this.collectMusic(Paths.get(uri));
        }

        try {
            FileSystem fs = FileSystems.getFileSystem(uri);
            return this.collectMusic(fs.getPath(resourceDir));
        } catch (java.nio.file.FileSystemNotFoundException e) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, java.util.Map.of())) {
                return this.collectMusic(fs.getPath(resourceDir));
            }
        }
    }

    private List<Music> collectMusic(Path dir) throws IOException {
        return this.listFiles(dir).stream().map(this::loadMusic).flatMap(Optional::stream).toList();
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

    private Optional<Music> loadMusic(Path path) {
        try {
            return Optional.of(new Music(path.toUri().toURL()));
        } catch (MalformedURLException e) {
            LOG.error("Error loading song: {}", path);
            return Optional.empty();
        }
    }
}
