package ulb.views.components;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import ulb.Configuration;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/** Reusable custom component displaying a Bugemon team in a grid. */
public class BugemonTeamView extends ComponentView {
    private static final int GRID_COLUMNS = 3;

    @FXML
    private GridPane gridPane;

    private Listener listener;

    public BugemonTeamView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_TEAM);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Clears and repopulates the grid with the alive members of the given team. */
    public void showTeam(Team bugemonTeam) {
        this.clearBugemons();

        List<PlayerBugemon> members = bugemonTeam.getMembers();
        for (int i = 0; i < members.size(); i++) {
            BugemonCardView card = new BugemonCardView(members.get(i));
            card.setListener(this.listener::onBugemonClicked);
            this.gridPane.add(card, i % GRID_COLUMNS, i / GRID_COLUMNS);
        }
    }

    /**
     * Clears the grid of the current Bugemons selected.
     */
    public void clearBugemons() {
        this.gridPane.getChildren().clear();
    }

    public interface Listener {

        void onBugemonClicked(PlayerBugemon bugemon);

    }
}
