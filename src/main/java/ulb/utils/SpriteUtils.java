package ulb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import ulb.Configuration;

public class SpriteUtils {
    
    private SpriteUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

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
