package ulb.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ulb.common.BugemonDTO;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable
 * grid.
 */
public class AllBugemonsGridView extends VBox {

    @FXML
    private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 10;
    private static final double IMAGE_SIZE = 96;

    private Function<BugemonDTO, Boolean> selectionChecker;
    private Consumer<BugemonDTO> setOnBugemonClicked;

    public AllBugemonsGridView() {
        URL url = getClass().getResource("/fxml/AllBugemonsGridView.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AllBugemonsGridView.fxml", e);
        }
    }

    /**
     * Sets the callback used to check if the bugemon given to the callback should
     * be marked as selected.
     */
    public void setSelectionChecker(Function<BugemonDTO, Boolean> checker) {
        this.selectionChecker = checker;
    }

    public void setOnClickCallback(Consumer<BugemonDTO> callback) {
        this.setOnBugemonClicked = callback;
    }

    public void showAll(List<BugemonDTO> bugemonList) {
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
        Image image = new Image("/png/" + bugemon.getSpriteURL());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        // keep image within a stackpane with fixed size so labels align perfectly
        StackPane imagePane = new StackPane(imageView);
        imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        Label nameLabel = new Label(bugemon.getName());
        nameLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        VBox cell = new VBox(2); // spacing exactly 2
        cell.setAlignment(Pos.CENTER);
        cell.getChildren().addAll(imagePane, nameLabel);
        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 3; -fx-background-color: transparent;");
        cell.setUserData(bugemon);

        if (selectionChecker != null && selectionChecker.apply(bugemon)) {
            select(cell);
        } else {
            unselect(cell);
        }

        if (this.setOnBugemonClicked != null) {
            cell.setOnMouseClicked((e) -> {
                BugemonDTO dto = (BugemonDTO) cell.getUserData();
                if (dto != null) {
                    this.setOnBugemonClicked.accept(dto);
                }
            });
        }

        return cell;
    }

    private void select(VBox cell) {
        StackPane imagePane = (StackPane) cell.getChildren().get(0);
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.setStyle("-fx-effect: dropshadow(three-pass-box, red, 5, 0.9, 0, 0);");
        cell.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-padding: 2; -fx-background-color: lightcoral;");
    }

    private void unselect(VBox cell) {
        StackPane imagePane = (StackPane) cell.getChildren().get(0);
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.setStyle("");
        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 3; -fx-background-color: transparent;");
    }
}
