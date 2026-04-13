package ulb.views.components;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

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
    public void showTeam(BugemonTeam bugemonTeam) {
        this.clearBugemons();

        List<Bugemon> aliveBugemons = bugemonTeam.aliveStream().toList();
        for (int i = 0; i < aliveBugemons.size(); i++) {
            BugemonCardView card = new BugemonCardView(aliveBugemons.get(i));
            card.setListener(this.listener::onBugemonClicked);
            this.gridPane.add(card, i % GRID_COLUMNS, i / GRID_COLUMNS);
        }
    }

    public void clearBugemons() {
        this.gridPane.getChildren().clear();
    }

    public interface Listener {

        void onBugemonClicked(Bugemon bugemon);

    }
}
