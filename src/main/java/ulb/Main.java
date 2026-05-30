package ulb;

import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;

/**
 * JavaFX entry point that bootstraps the Bugemon game.
 *
 * <p>
 * Installs the SLF4J bridge over {@code java.util.logging}, loads CSS stylesheets and the pixel-art font, then wires
 * the database, services, player state and {@link ulb.controllers.MetaController} before handing control to JavaFX.
 */
public class Main extends Application {

    /**
     * Application entry point.
     *
     * <p>
     * Redirects all {@code java.util.logging} output through SLF4J, then delegates to
     * {@link Application#launch(String...)} to start the JavaFX runtime.
     */
    void main() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        launch();
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
        URL url = Main.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("CSS resource not found: " + path);
        }
        return url.toExternalForm();
    }

    /**
     * Builds the application-wide window backdrop: a soft, light radial gradient painted as the {@link Scene} fill.
     *
     * <p>
     * Because every view replaces the scene root (and the menu roots are transparent), painting the gradient on the
     * scene itself shows it uniformly behind all screens without fighting per-view CSS. It is kept light so the dark
     * menu text stays fully readable.
     *
     * @return the radial gradient used as the scene fill
     */
    private static RadialGradient buildBackground() {
        return new RadialGradient(0, 0, 0.5, 0.35, 0.9, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#f7f9fc")),
                new Stop(0.55, Color.web("#e4ebf5")), new Stop(1, Color.web("#cdd8e8")));
    }

    /**
     * Initialises the primary {@link Stage} and wires all game components.
     *
     * <p>
     * Steps performed:
     * <ul>
     * <li>Loads the pixel-art font from {@code /fonts/boldpixels.ttf}.</li>
     * <li>Applies the ordered CSS stylesheets defined in {@link Configuration.Paths.Css#LOAD_ORDER}.</li>
     * <li>Initialises the database via {@link ulb.bootstrap.GameBootstrapper}.</li>
     * <li>Creates all services, player state, and starts the {@link ulb.controllers.MetaController}.</li>
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
        stage.setFullScreenExitHint("");

        Scene scene = new Scene(new StackPane());
        scene.setFill(buildBackground());
        for (String path : Configuration.Paths.Css.LOAD_ORDER) {
            scene.getStylesheets().add(loadStylesheet(path));
        }
        stage.setScene(scene);

        MetaController metaController = new MetaController(stage);
        metaController.start();

    }
}
