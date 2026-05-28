package bugemon.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import bugemon.common.Configuration;

/**
 * Utility class for copying custom Bugemon sprite files to the application's sprite directory.
 */
public class SpriteUtils {

    private SpriteUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Copies the sprite at {@code spriteUrl} to the configured sprites directory under {@code spriteFileName}.
     *
     * <p>
     * If the source is a local file that is already at the target path, the copy is skipped. The destination directory
     * is created if it does not exist. Any existing file at the target is replaced.
     *
     * @param spriteUrl
     *            the source URL of the sprite image
     * @param spriteFileName
     *            the file name (not path) to use in the destination directory
     * @throws IOException
     *             if the file cannot be copied to the destination
     */
    public static void saveSpriteFile(URL spriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(Configuration.Paths.SPRITES);
        if (!Files.exists(dirDestination)) {
            Files.createDirectories(dirDestination);
        }
        Path fileTarget = dirDestination.resolve(spriteFileName);

        if ("file".equalsIgnoreCase(spriteUrl.getProtocol())) {
            try {
                Path sourcePath = Paths.get(spriteUrl.toURI());
                if (Files.exists(fileTarget) && Files.isSameFile(sourcePath, fileTarget)) {
                    return;
                }
            } catch (java.net.URISyntaxException e) {
                // we ignore and try to copy the file anyway
            }
        }

        try (InputStream in = spriteUrl.openStream()) {
            Files.copy(in, fileTarget, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible to save sprite file: " + fileTarget, e);
        }
    }
}
