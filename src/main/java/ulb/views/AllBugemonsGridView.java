package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import ulb.models.bugemon.Bugemon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable grid.
 */
public class AllBugemonsGridView extends VBox {
    @FXML
    private GridPane gridPane;

    private static final double CELL_TARGET_WIDTH = 112;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;
    private List<Bugemon> displayedBugemons = List.of();

    /**
     * Constructor of the AllBugemonsGridView class. It loads the FXML layout and initializes the view.
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
     * Sets the callback used to check if the bugemon given to the callback should be marked as selected.
     */
    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    /**
     * Sets the callback used to handle clicks on bugemon cells. The callback receives the
     * {@link ulb.common.dto.BugemonDTO} of the clicked cell.
     *
     * @param callback a {@code Consumer<BugemonDTO>} callback to be called when a bugemon cell is clicked, receiving
     *                 the {@link ulb.common.dto.BugemonDTO} of the clicked cell; must not be {@code null}.
     */
    public void setOnClickCallback(Consumer<Bugemon> callback) {
        this.onBugemonClicked = callback;
    }

    /**
     * Displays all available Bugemons in the grid view.
     *
     * @param bugemonList the list of all available Bugemons to be displayed
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

            BugemonCard cell = new BugemonCard(bugemon);
            cell.setPrefWidth(CELL_TARGET_WIDTH);

            if (this.selectionChecker != null) {
                cell.setSelected(this.selectionChecker.apply(bugemon));
            }

            if (this.onBugemonClicked != null) {
                cell.setOnClick(this.onBugemonClicked);
            }

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

}
