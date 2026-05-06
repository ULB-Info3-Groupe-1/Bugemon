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

    /**
     * Default constructor.
     */
    public BugemonTeamView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_TEAM);
    }

    /**
     * Sets the listener to be notified when a Bugemon is clicked.
     *
     * @param listener
     *            the listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Clears and repopulates the grid with the alive members of the given team.
     *
     * @param bugemonTeam
     *            the team to display
     */
    public void showTeam(BugemonTeam bugemonTeam) {
        this.clearBugemons();

        List<Bugemon> aliveBugemons = bugemonTeam.aliveStream().toList();
        for (int i = 0; i < aliveBugemons.size(); i++) {
            BugemonCardView card = new BugemonCardView(aliveBugemons.get(i));
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

        /**
         * Dispatches a click event to the controller.
         *
         * @param bugemon
         *            the clicked Bugemon
         */
        void onBugemonClicked(Bugemon bugemon);

    }
}
