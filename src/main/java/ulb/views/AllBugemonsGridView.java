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

    /**
     * Constructor of the AllBugemonsGridView class. It loads the FXML layout and initializes the
     * view.
     */
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
     * Sets the callback used to check if the bugemon given to the callback should be marked as
     * selected.
     */
    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    /**
     * Sets the callback used to handle clicks on bugemon cells. The callback receives the
     * {@link ulb.common.dto.BugemonDTO} of the clicked cell.
     *
     * @param callback
     *            a {@code Consumer<BugemonDTO>} callback to be called when a bugemon cell is
     *            clicked, receiving the {@link ulb.common.dto.BugemonDTO} of the clicked cell; must
     *            not be {@code null}.
     */
    public void setOnClickCallback(Consumer<Bugemon> callback) {
        this.onBugemonClicked = callback;
    }

    /**
     * Displays all available Bugemons in the grid view.
     *
     * @param bugemonList
     *            the list of all available Bugemons to be displayed
     */
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

    /**
     * Creates a cell for a Bugemon in the grid view, containing the image and name of the Bugemon.
     * If the Bugemon is null, it displays an unknown image and an empty name.
     *
     * @param bugemon
     *            the BugemonDTO representing the Bugemon to be displayed in the cell
     * @return a VBox containing the image and name of the Bugemon to be displayed in the grid view
     */
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

    /**
     * Marks the given cell as selected by changing its style to indicate selection.
     *
     * @param cell
     *            the VBox cell to be marked as selected, containing the image and name of the
     *            Bugemon to be styled as selected
     */
    private void select(VBox cell) {
        StackPane imagePane = (StackPane) cell.getChildren().get(0); // TODO: Could break code with
                                                                     // an exeption
                                                                     // "IndexOutOfBoundsException"
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.getStyleClass().add("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell");
        cell.getStyleClass().add("bugemon-cell-selected");
    }

    /**
     * Marks the given cell as unselected by changing its style to indicate it is not selected.
     *
     * @param cell
     *            the VBox cell to be marked as unselected, containing the image and name of the
     *            Bugemon to be styled as unselected
     */
    private void unselect(VBox cell) {
        StackPane imagePane = (StackPane) cell.getChildren().get(0);
        ImageView iv = (ImageView) imagePane.getChildren().get(0);
        iv.getStyleClass().remove("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell-selected");
        cell.getStyleClass().add("bugemon-cell");
    }
}
