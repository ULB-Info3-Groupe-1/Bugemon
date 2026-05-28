package bugemon.client.views.components;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import bugemon.common.Configuration;
import bugemon.common.dto.display.BugemonDisplayDTO;

/**
 * Reusable custom component displaying a Bugemon team in a fixed {@value #GRID_COLUMNS}-column grid. Click events on
 * individual cards are forwarded through {@link Listener}.
 */
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

    /**
     * Clears the grid and fills it with a card for each team member.
     *
     * @param members
     *            the team members to display, in slot order
     */
    public void showTeam(List<BugemonDisplayDTO> members) {
        this.clearBugemons();

        for (int i = 0; i < members.size(); i++) {
            BugemonCardView card = new BugemonCardView(members.get(i));
            card.setListener(this.listener::onBugemonSelected);
            this.gridPane.add(card, i % GRID_COLUMNS, i / GRID_COLUMNS);
        }
    }

    public void clearBugemons() {
        this.gridPane.getChildren().clear();
    }

    /** Callback interface for team grid click interactions. */
    public interface Listener {
        /** Called when the player clicks a Bugemon card in the team grid. */
        void onBugemonSelected(BugemonDisplayDTO bugemon);
    }
}
