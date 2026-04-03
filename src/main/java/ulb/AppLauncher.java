package ulb;

/**
 * Neutral JAR entry point. Does not extend {@link javafx.application.Application}, which avoids the JVM's early JavaFX
 * runtime check that would fail when JavaFX modules are absent from the module path.
 */
public class AppLauncher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
