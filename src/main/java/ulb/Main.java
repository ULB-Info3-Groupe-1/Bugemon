package ulb;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;

/**
 * JavaFX {@link Application} subclass that bootstraps the Bugemon game.
 *
 * <p>
 * This class serves as the JavaFX entry point. It is responsible for:
 * <ol>
 *   <li>Configuring the primary {@link Stage} (title, minimum size, maximised
 *       state).</li>
 *   <li>Instantiating the {@link MetaController}, which in turn loads all game
 *       resources (JSON data files) and initialises every screen controller.</li>
 *   <li>Navigating to the {@link Window#MAIN_MENU} as the first visible
 *       screen.</li>
 * </ol>
 *
 * <p>
 * The actual JVM entry point is {@link AppLauncher#main(String[])}, which
 * delegates to {@link #main(String[])} here. This indirection is required so
 * that the application can be launched from a fat JAR without the JVM
 * performing a JavaFX runtime check on the main class.
 * </p>
 *
 * <p>
 * If an {@link IOException} occurs during initialisation (e.g., a missing
 * resource file), the stack trace is printed and the process exits with
 * status code {@code 1}.
 * </p>
 *
 * @see AppLauncher
 * @see MetaController
 */
public class Main extends Application {
    private static final String STAGE_TITLE = "Bugemon";

    /**
     * Launches the JavaFX application.
     *
     * <p>
     * Delegates directly to {@link Application#launch(String...)} which
     * bootstraps the JavaFX toolkit and eventually calls
     * {@link #start(Stage)}.
     * </p>
     *
     * @param args command-line arguments forwarded to the JavaFX launcher
     *             (not used by this application).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialises and displays the primary application window.
     *
     * <p>
     * This method is invoked by the JavaFX runtime on the JavaFX Application
     * Thread after the toolkit has been initialised. It performs the following
     * steps:
     * <ol>
     *   <li>Maximises the stage and sets a minimum size of 60 % × 50 % of the
     *       primary screen's visual bounds, so the UI never becomes too small to
     *       use on any resolution.</li>
     *   <li>Sets the window title to {@value #STAGE_TITLE}.</li>
     *   <li>Creates the {@link MetaController}, passing the stage so it can
     *       swap scenes during navigation.</li>
     *   <li>Triggers the initial navigation to {@link Window#MAIN_MENU}.</li>
     * </ol>
     *
     * @param primaryStage
     * @param primaryStage the primary {@link Stage} provided by the JavaFX
     *                     runtime; must not be {@code null}.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            InputStream fontStream = Main.class.getResourceAsStream("/fonts/boldpixels.ttf");
            if (fontStream != null) {
                Font.loadFont(fontStream, 16);
            }

            primaryStage.setMaximized(true);
            Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
            primaryStage.setMinWidth(rectangle2d.getWidth() * 0.6);
            primaryStage.setMinHeight(rectangle2d.getHeight() * 0.5);
            primaryStage.setTitle(STAGE_TITLE);

            MetaController controller = new MetaController(primaryStage);
            controller.switchTo(Window.MAIN_MENU);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
