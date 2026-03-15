<<<<<<< HEAD
package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ulb.common.dto.BugemonDTO;
import ulb.fx_controllers.CreateTeamFXController;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.TeamService;
import ulb.utils.Parser;

/**
 * Controller responsible for the team creation screen, where the player
 * assembles their {@link ulb.models.bugemon_team.BugemonTeam} before entering
 * a combat.
 *
 * <p>
 * {@code CreateTeamController} manages two synchronised views:
 * <ul>
 *   <li>A grid of all available {@link ulb.models.bugemon.Bugemon}s loaded
 *       from the game's JSON data files.</li>
 *   <li>The player's current team, showing up to
 *       {@code BugemonTeam.MAX_SIZE} selected members.</li>
 * </ul>
 * Clicking a Bugemon toggles its membership: if the Bugemon is already in the
 * team it is removed; otherwise it is added (provided the team is not full).
 * </p>
 *
 * <p>
 * Once satisfied with their team composition, the player can start either an
 * automatic combat (via {@link #startAutoCombat()}) or a manual combat (via
 * {@link #startManualCombat()}), both of which delegate to the
 * {@link MetaController}.
 * </p>
 *
 * @see MetaController
 * @see ulb.fx_controllers.CreateTeamFXController
 * @see Controller
 * @see ulb.models.bugemon_team.BugemonTeam
 */
public class CreateTeamController extends Controller {
    private final TeamService teamService;

    public CreateTeamController(MetaController metaController, TeamService teamService) {
        super(metaController);

        this.teamService = teamService;
    }

    public void onBugemonClicked(String id) {
        // à refactor
    }

    private void updateAllBugemonsView() {
        // à refactor

    }

    private void updateBugemonsTeamView() {
        // à refactor

    }

    public void startAutoCombat() {
        // à refactor
    }

    public void startManualCombat() {
        // à refactor
    }

    public boolean checkBugemonInTeam(String bugemonId) {
        // à refactor
    }

    private boolean teamIsEmpty() {
        // à refactor
    }
}
=======
package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ulb.common.dto.BugemonDTO;
import ulb.fx_controllers.CreateTeamFXController;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.PlayerService;
import ulb.utils.Parser;

/**
 * Controller responsible for the team creation screen, where the player
 * assembles their {@link ulb.models.bugemon_team.BugemonTeam} before entering
 * a combat.
 *
 * <p>
 * {@code CreateTeamController} manages two synchronised views:
 * <ul>
 *   <li>A grid of all available {@link ulb.models.bugemon.Bugemon}s loaded
 *       from the game's JSON data files.</li>
 *   <li>The player's current team, showing up to
 *       {@code BugemonTeam.MAX_SIZE} selected members.</li>
 * </ul>
 * Clicking a Bugemon toggles its membership: if the Bugemon is already in the
 * team it is removed; otherwise it is added (provided the team is not full).
 * </p>
 *
 * <p>
 * Once satisfied with their team composition, the player can start either an
 * automatic combat (via {@link #startAutoCombat()}) or a manual combat (via
 * {@link #startManualCombat()}), both of which delegate to the
 * {@link MetaController}.
 * </p>
 *
 * @see MetaController
 * @see ulb.fx_controllers.CreateTeamFXController
 * @see Controller
 * @see ulb.models.bugemon_team.BugemonTeam
 */
public class CreateTeamController extends Controller {
    private final PlayerService playerService;

    public CreateTeamController(MetaController metaController, PlayerService playerService) {
        super(metaController);

        this.playerService = playerService;
    }

    public List<BugemonDTO> getBugemonsDTO() {
        List<Bugemon> bugemons = Parser.getInstance().getBugemons();
        return bugemons.stream()
                       .map(bugemon -> (BugemonDTO) bugemon)
                       .collect(Collectors.toList());
    }

    public void onBugemonClicked(String id) {
        // à refactor
    }

    public void startAutoCombat() {
        // à refactor
    }

    public void startManualCombat() {
        // à refactor
    }

    public boolean checkBugemonInTeam(String bugemonId) {
        // à refactor
        return false;
    }

    private boolean teamIsEmpty() {
        // à refactor
        return false;
    }
}
>>>>>>> c9e599c (refactor of the views into proper fx controllers)
