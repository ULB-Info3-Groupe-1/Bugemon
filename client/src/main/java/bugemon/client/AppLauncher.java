package bugemon.client;

/**
 * Neutral JAR entry point that delegates to {@link Main}.
 *
 * <p>
 * Does not extend {@link javafx.application.Application}, which avoids the JVM's early JavaFX runtime check that would
 * fail when JavaFX modules are absent from the module path at launch time. The fat-JAR manifest should point to this
 * class rather than {@link Main}.
 */
public class AppLauncher {

    /**
     * Delegates immediately to {@link Main#main(String[])}.
     *
     * @param args
     *            command-line arguments forwarded to the JavaFX application
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
