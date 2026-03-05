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
import javafx.geometry.Pos;
import javafx.scene.control.Label;

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

            VBox cell = createBugemonCell(bugemon);

            gridPane.add(cell, col, row);
        }
    }

    private VBox createBugemonCell(BugemonDTO bugemon) {
        Image image = (bugemon != null)
                ? new Image(bugemon.getSpriteURL())
                : this.UNKNOWN_IMAGE;

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);

        StackPane imagePane = new StackPane(imageView);
        imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        String name = (bugemon != null) ? bugemon.getName() : "Vide";
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        VBox cell = new VBox(2);
        cell.setAlignment(Pos.CENTER);
        cell.getChildren().addAll(imagePane, nameLabel);
        
        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 3;");
        cell.setUserData(bugemon);

        return cell;
    }

}

