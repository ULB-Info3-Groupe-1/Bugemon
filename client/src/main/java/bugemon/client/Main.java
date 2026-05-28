package bugemon.client;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import bugemon.common.Configuration;
import bugemon.client.controllers.MetaController;

/**
 * JavaFX entry point that bootstraps the Bugemon game.
 *
 * <p>
 * Installs the SLF4J bridge over {@code java.util.logging}, loads CSS stylesheets and the pixel-art font, then wires
 * the database, services, player state and {@link bugemon.client.controllers.MetaController} before handing control to JavaFX.
 */
public class Main extends Application {

    /**
     * Application entry point.
     *
     * <p>
     * Redirects all {@code java.util.logging} output through SLF4J, then delegates to
     * {@link Application#launch(String...)} to start the JavaFX runtime.
     *
     * @param args
     *            command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        launch(args);
    }

    /**
     * Resolves a classpath CSS resource to an external-form URL string suitable for
     * {@link javafx.scene.Scene#getStylesheets()}.
     *
     * @param path
     *            classpath-relative path to the CSS file (e.g. {@code "/css/base.css"})
     * @return the external-form URL string of the resource
     * @throws IllegalStateException
     *             if no resource exists at {@code path}
     */
    private static String loadStylesheet(String path) {
        java.net.URL url = Main.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("CSS resource not found: " + path);
        }
        return url.toExternalForm();
    }

    /**
     * Initialises the primary {@link Stage} and wires all game components.
     *
     * <p>
     * Steps performed:
     * <ul>
     * <li>Loads the pixel-art font from {@code /fonts/boldpixels.ttf}.</li>
     * <li>Applies the ordered CSS stylesheets defined in {@link Configuration.Paths.Css#LOAD_ORDER}.</li>
     * <li>Initialises the database via {@link bugemon.server.bootstrap.GameBootstrapper}.</li>
     * <li>Creates all services, player state, and starts the {@link bugemon.client.controllers.MetaController}.</li>
     * </ul>
     *
     * @param stage
     *            the primary stage provided by the JavaFX runtime
     * @throws Exception
     *             if any initialisation step fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        InputStream fontStream = Main.class.getResourceAsStream("/fonts/boldpixels.ttf");
        if (fontStream != null) {
            Font.loadFont(fontStream, Configuration.Ui.FONT_SIZE);
        }

        stage.setTitle(Configuration.Ui.STAGE_TITLE);
        stage.setMaximized(true);

        Scene scene = new Scene(new StackPane());
        for (String path : Configuration.Paths.Css.LOAD_ORDER) {
            scene.getStylesheets().add(loadStylesheet(path));
        }
        stage.setScene(scene);

        MetaController metaController = new MetaController(stage);
        metaController.start();

    }
}
