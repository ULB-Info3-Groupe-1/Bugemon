package ulb.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ulb.common.BugemonDTO;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable
 * grid.
 */
public class BugemonTeamView extends VBox {

    @FXML
    private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 3;
    private static final double IMAGE_SIZE = 96;

    // Unknown image if no Bugemon available
    private final Image UNKNOWN_IMAGE = new Image("/png/unknown.png");

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
    }

    public void showTeam(List<BugemonDTO> bugemonList) {
        this.gridPane.getChildren().clear();

        for (int i = 0; i < bugemonList.size(); i++) {

            BugemonDTO bugemon = bugemonList.get(i);

            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;

            StackPane cell = createImageView(bugemon);

            gridPane.add(cell, col, row);
        }
    }

    private StackPane createImageView(BugemonDTO bugemon) {
        Image image = (bugemon != null)
                ? new Image("/png/" + bugemon.getSpriteURL()) // TODO : Retirer le /png/
                : this.UNKNOWN_IMAGE;

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);

        StackPane pane = new StackPane(imageView);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5;");
        pane.setUserData(bugemon);

        return pane;
    }

}

