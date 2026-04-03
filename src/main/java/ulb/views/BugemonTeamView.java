package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable grid.
 */
public class BugemonTeamView extends VBox {
    @FXML
    private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 3;

    private Listener listener;

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

    public void showTeam(BugemonTeam bugemonTeam) {
        this.gridPane.getChildren().clear();

        List<Bugemon> aliveBugemons = bugemonTeam.aliveStream().toList();
        for (int i = 0; i < aliveBugemons.size(); i++) {
            Bugemon bugemon = aliveBugemons.get(i);
            BugemonCardView cell = new BugemonCardView(bugemon);
            if (this.listener != null) {
                cell.setOnClick(this.listener::onBugemonClicked);
            }
            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;
            this.gridPane.add(cell, col, row);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        void onBugemonClicked(Bugemon bugemon);

    }
}
