package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;
import ulb.views.MainMenuView;

/**
 * Controller responsible for the main menu screen.
 *
 * <p>
 * {@code MainMenuController} manages the first screen the player sees when
 * launching the application. From this screen the player can navigate to the
 * team creation screen to build their {@link ulb.models.bugemon_team.BugemonTeam}
 * before starting a combat.
 * </p>
 *
 * <p>
 * User interactions originating from {@link MainMenuView} are forwarded to
 * this controller via callback methods (e.g., {@link #createTeam()}), which
 * then delegate navigation decisions to the {@link MetaController}.
 * </p>
 *
 * @see MetaController
 * @see MainMenuView
 * @see Controller
 */
public class MainMenuController extends Controller<MainMenuView> {
    private final PlayerService playerService;

    /**
     * Constructs a {@code MainMenuController}, initialises its {@link MainMenuView},
     * and registers this controller as the view's event handler.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its
     * scene graph is ready before the controller is used for the first time.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation; must not be {@code null}.
     * @throws IOException if the {@link MainMenuView} fails to load its FXML
     *                     resource.
     */
    public MainMenuController(MetaController metaController, PlayerService playerService)
            throws IOException {
        super(metaController, new MainMenuView());
        this.playerService = playerService;

        this.view.setOnCreateTeam(this::createTeam);
        this.view.setOnNoTower(this::launchNoTower);
        this.view.setOnQuit(this::quit);
        this.view.setOnStartAutoCombat(this::startAutoCombat);
        this.view.setOnStartManualCombat(this::startManualCombat);
    }

    /**
     * Callback invoked when the player requests to create or edit their team.
     *
     * <p>
     * Delegates to {@link MetaController#switchTo(Window)} to navigate to the
     * {@link Window#CREATE_TEAM} screen.
     * </p>
     */
    public void createTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    /** Callback invoked when the player requests to launch the NO Tower mode. */
    public void launchNoTower() {
        this.metaController.switchTo(Window.NOTOWER);
    }

    /**
     * Callback invoked when the player wants to quit the application.
     */
    public void quit() {
        javafx.application.Platform.exit();
    }

    /** Launches an automatic combat session. */
    public void startAutoCombat() {
        if (this.playerService.isActiveTeamEmpty()) {
            showNoTeamAlert();
        } else {
            this.metaController.switchTo(Window.AUTOMATIC_COMBAT);
        }
    }

    /** Launches a manual combat session. */
    public void startManualCombat() {
        if (this.playerService.isActiveTeamEmpty()) {
            showNoTeamAlert();
        } else {
            this.metaController.switchTo(Window.MANUAL_COMBAT);
        }
    }

    private void showNoTeamAlert() {
        view.showAlert("Aucune équipe active",
                       "Veuillez créer ou charger une équipe avant de lancer un combat.");
    }
}
