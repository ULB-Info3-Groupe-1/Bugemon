package ulb.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable
 * grid.
 */
public class BugemonTeamView extends VBox {
    @FXML private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 3;

    public BugemonTeamView() {
        URL url = getClass().getResource("/fxml/BugemonTeam.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BugemonTeamView.fxml", e);
        }

        getStylesheets().add(getClass().getResource("/css/bugemon-team.css").toExternalForm());
    }

    /**
     * Displays the alive bugemon in the given team.
     * @param bugemonTeam the team to display
     */
    public void showAlive(BugemonTeam bugemonTeam) {
        this.gridPane.getChildren().clear();

        List<Bugemon> aliveBugemons = bugemonTeam.aliveStream().toList();
        for (int i = 0; i < aliveBugemons.size(); i++) {
            Bugemon bugemon = aliveBugemons.get(i);
            BugemonCell cell = new BugemonCell(bugemon);
            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;
            this.gridPane.add(cell, col, row);
        }
    }

    /**
     * Displays the bugemons in the given team.
     * @param bugemonTeam the team to display
     */
    public void showTeam(BugemonTeam bugemonTeam) {
        this.gridPane.getChildren().clear();

        for (int i = 0; i < BugemonTeam.MAX_SIZE; i++) {
            Optional<Bugemon> bugemon = bugemonTeam.get(i);
            BugemonCell cell = bugemon.map(b -> new BugemonCell(b)).orElse(new BugemonCell());

            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;
            this.gridPane.add(cell, col, row);
        }
    }
}
