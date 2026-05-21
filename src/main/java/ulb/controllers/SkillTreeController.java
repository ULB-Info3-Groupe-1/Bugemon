package ulb.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.common.dto.BugemonDisplayDTO;
import ulb.models.player.PlayerBugemon;
import ulb.models.player.PlayerState;
import ulb.models.team.Team;
import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.services.exceptions.TeamNameEmptyException;
import ulb.services.exceptions.TeamNotFoundException;
import ulb.views.ManageTeamView;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;

public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {
    public SkillTreeController(MetaController metaController) {

    }

    // TODO:
    // Left click to add / consumme 1 skill point on the node clicked if
    // eligible
    //
    // Right click return one point to the player and he get refund.
    //
    // For thos click the state are updated and needs to call refreshSkillState of the view!!!
}
