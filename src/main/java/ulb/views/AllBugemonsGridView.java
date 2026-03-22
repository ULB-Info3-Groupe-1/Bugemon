package ulb.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable
 * grid.
 */
public class AllBugemonsGridView extends VBox {
    @FXML private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 10;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;

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
            throw new RuntimeException("Failed to load AllBugemonsGridView.fxml", e);
        }

        getStylesheets().add(getClass().getResource("/css/all-bugemons-grid.css").toExternalForm());
    }

    /**
     * Sets the callback used to check if the bugemon given to the callback should
     * be marked as selected.
     */
    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    /**
     * Sets the callback used to handle clicks on bugemon cells. The callback
     * receives the {@link ulb.common.dto.BugemonDTO} of the clicked cell.
     *
     * @param callback a {@code Consumer<BugemonDTO>} callback to be called when
     *                 a bugemon cell is clicked, receiving the
     *                 {@link ulb.common.dto.BugemonDTO} of the clicked cell;
     *                 must not be {@code null}.
     */
    public void setOnClickCallback(Consumer<Bugemon> callback) {
        this.onBugemonClicked = callback;
    }

    /**
     * Displays all available Bugemons in the grid view.
     * @param bugemonList the list of all available Bugemons to be displayed
     */
    public void showAll(List<Bugemon> bugemonList) {
        this.gridPane.getChildren().clear();

        for (int i = 0; i < bugemonList.size(); i++) {
            Bugemon bugemon = bugemonList.get(i);

            int row = i / IMAGES_PER_ROW;
            int col = i % IMAGES_PER_ROW;

            BugemonCell cell = createBugemonCell(bugemon);

            gridPane.add(cell, col, row);
        }
    }

    /**
     * Creates a cell for a Bugemon in the grid view, containing the image and name of the Bugemon.
     * @param bugemon the Bugemon to be displayed in the cell
     * @return a BugemonCell containing the image and name of the Bugemon to be displayed in the
     *         grid view
     */
    private BugemonCell createBugemonCell(Bugemon bugemon) {
        BugemonCell cell = new BugemonCell(bugemon);

        // Apply selection state
        boolean isSelected = selectionChecker != null && selectionChecker.apply(bugemon);
        cell.setSelected(isSelected);

        // Set click handler
        if (this.onBugemonClicked != null) {
            cell.setOnMouseClicked(e -> {
                Bugemon b = (Bugemon)cell.getBugemonData();
                if (b != null) {
                    this.onBugemonClicked.accept(b);
                }
            });
        }

        return cell;
    }
}
