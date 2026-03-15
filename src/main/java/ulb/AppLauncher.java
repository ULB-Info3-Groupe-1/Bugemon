package ulb;

/**
 * Neutral entry point that allows the Bugemon application to be launched from
 * an executable JAR file.
 *
 * <p>
 * When a JavaFX application is packaged as a fat JAR and the manifest's
 * {@code Main-Class} attribute points directly to a class that extends
 * {@link javafx.application.Application}, the JVM performs an early JavaFX
 * runtime availability check and throws an error if the JavaFX modules are not
 * on the module path. This class sidesteps that check by acting as an
 * intermediary: because {@code AppLauncher} does <em>not</em> extend
 * {@link javafx.application.Application}, the JVM skips the check entirely,
 * and the JavaFX toolkit is initialised only when {@link Main#main(String[])}
 * calls {@link javafx.application.Application#launch(String...)}.
 * </p>
 *
 * <p>
 * The {@code Main-Class} entry in the JAR manifest (or the {@code mainClass}
 * field in the build descriptor) should therefore point to this class rather
 * than to {@link Main}.
 * </p>
 *
 * @see Main
 */
public class AppLauncher {
    /**
     * JVM entry point of the Bugemon application.
     *
     * <p>
     * Delegates immediately to {@link Main#main(String[])} which, in turn,
     * calls {@link javafx.application.Application#launch(String...)} to
     * bootstrap the JavaFX runtime and display the primary window.
     * </p>
     *
     * @param args command-line arguments forwarded verbatim to
     *             {@link Main#main(String[])}; currently unused by the
     *             application.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
