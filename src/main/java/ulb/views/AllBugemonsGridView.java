package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable grid.
 */
public class AllBugemonsGridView extends VBox {
    @FXML
    private GridPane gridPane;

    private static final double IMAGE_SIZE = 96;
    private static final double CELL_TARGET_WIDTH = 112;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;
    private List<Bugemon> displayedBugemons = List.of();

    public AllBugemonsGridView() {
        URL url = getClass().getResource("/fxml/AllBugemonsGrid.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load AllBugemonsGridView.fxml", e);
        }

        getStylesheets().add(getClass().getResource("/css/all-bugemons-grid.css").toExternalForm());

        // Keep grid width in sync with the available area to avoid clipped columns.
        widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (!this.displayedBugemons.isEmpty()) {
                this.renderGrid();
            }
        });
    }

    /**
     * Sets the callback used to check if the bugemon given to the callback should be marked as selected.
     */
    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    public void setOnClickCallback(Consumer<Bugemon> callback) {
        this.onBugemonClicked = callback;
    }

    public void showAll(List<Bugemon> bugemonList) {
        this.displayedBugemons = (bugemonList == null) ? List.of() : bugemonList;
        this.renderGrid();
    }

    private void renderGrid() {
        this.gridPane.getChildren().clear();
        if (this.displayedBugemons.isEmpty()) {
            return;
        }

        int imagesPerRow = this.computeImagesPerRow();

        for (int i = 0; i < this.displayedBugemons.size(); i++) {
            Bugemon bugemon = this.displayedBugemons.get(i);

            int row = i / imagesPerRow;
            int col = i % imagesPerRow;

            VBox cell = this.createBugemonCell(bugemon);

            this.gridPane.add(cell, col, row);
        }
    }

    private int computeImagesPerRow() {
        double width = getWidth();
        if (width <= 0) {
            width = getPrefWidth();
        }
        if (width <= 0) {
            return 6;
        }

        int columns = (int) Math.floor(width / CELL_TARGET_WIDTH);
        return Math.max(1, columns);
    }

    private VBox createBugemonCell(Bugemon bugemon) {
        Image image = new Image(bugemon.getSpriteURL(), IMAGE_SIZE, IMAGE_SIZE, true, false);

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        // keep image within a stackpane with fixed size so labels align perfectly
        StackPane imagePane = new StackPane(imageView);
        imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        Label nameLabel = new Label(bugemon.getName());
        nameLabel.getStyleClass().add("bugemon-cell-name");

        VBox cell = new VBox(2); // spacing exactly 2
        cell.setAlignment(Pos.CENTER);
        cell.setPrefWidth(CELL_TARGET_WIDTH);
        cell.getChildren().addAll(imagePane, nameLabel);
        cell.setUserData(bugemon);

        if (this.selectionChecker != null && this.selectionChecker.apply(bugemon)) {
            this.select(cell);
        } else {
            this.unselect(cell);
        }

        if (this.onBugemonClicked != null) {
            cell.setOnMouseClicked(e -> {
                Bugemon b = (Bugemon) cell.getUserData();
                if (b != null) {
                    this.onBugemonClicked.accept(b);
                }
            });
        }

        return cell;
    }

    private void select(VBox cell) {
        // TODO: Could break code with an exception "IndexOutOfBoundsException"
        StackPane imagePane = (StackPane) cell.getChildren().get(0);
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.getStyleClass().add("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell");
        cell.getStyleClass().add("bugemon-cell-selected");
    }

    private void unselect(VBox cell) {
        StackPane imagePane = (StackPane) cell.getChildren().get(0);
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.getStyleClass().remove("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell-selected");
        cell.getStyleClass().add("bugemon-cell");
    }
}
