package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;

/**
 * Reusable custom component displaying all the bugemons inside of a scrollable grid.
 */
public class AllBugemonsGridView extends VBox {
    @FXML
    private FlowPane flowPane;

    private static final double CELL_WIDTH = 112;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;

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
     * @param callback
     *            a {@code Consumer<BugemonDTO>} callback to be called when a bugemon cell is clicked, receiving the
     *            {@link ulb.common.dto.BugemonDTO} of the clicked cell; must not be {@code null}.
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
        this.flowPane.getChildren().clear();

        if (bugemonList.isEmpty()) {
            return;
        }

        for (Bugemon bugemon : bugemonList) {
            BugemonCardView card = this.createBugemonCard(bugemon);
            this.flowPane.getChildren().add(card);
        }
    }

    private BugemonCardView createBugemonCard(Bugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.setPrefWidth(CELL_WIDTH);

        if (this.selectionChecker != null) {
            card.setSelected(this.selectionChecker.apply(bugemon));
        }

        if (this.onBugemonClicked != null) {
            card.setOnClick(this.onBugemonClicked);
        }

        return card;
    }
}
