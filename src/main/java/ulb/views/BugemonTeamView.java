package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable grid.
 */
public class BugemonTeamView extends VBox {
    @FXML
    private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 3;

    private Consumer<BugemonDTO> onBugemonClicked;

    public BugemonTeamView() {
        URL url = getClass().getResource("/fxml/BugemonTeam.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load BugemonTeamView.fxml", e);
        }

        getStylesheets().add(getClass().getResource("/css/bugemon-team.css").toExternalForm());
    }

    /**
     * Displays the player's current team in the team view.
     *
     * @param bugemonList
     */
    public void showTeam(BugemonTeam bugemonTeam) {
        this.gridPane.getChildren().clear();

        List<Bugemon> aliveBugemons = bugemonTeam.aliveStream().toList();
        for (int i = 0; i < aliveBugemons.size(); i++) {
            Bugemon bugemon = aliveBugemons.get(i);
            BugemonCardView cell = new BugemonCardView(bugemon);
            if (this.onBugemonClicked != null) {
                cell.setOnClick(this.onBugemonClicked::accept);
            }
            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;
            this.gridPane.add(cell, col, row);
        }
    }

    /**
     * Sets the callback used to handle clicks on bugemon cells. The callback receives the
     * {@link ulb.common.dto.BugemonDTO} of the clicked cell.
     *
     * @param callback
     *            a {@code Consumer<BugemonDTO>} callback to be called when a bugemon cell is clicked, receiving the
     *            {@link ulb.common.dto.BugemonDTO} of the clicked cell; must not be {@code null}.
     */
    public void setOnClickCallback(Consumer<BugemonDTO> callback) {
        this.onBugemonClicked = callback;
    }
}
