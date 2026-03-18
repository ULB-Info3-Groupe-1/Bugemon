package ulb.views;

import java.io.IOException;
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
 * Reusable custom component displaying all the bugemons inside of a scrollable
 * grid.
 */
public class AllBugemonsGridView extends VBox {
    @FXML private GridPane gridPane;

    private static final int IMAGES_PER_ROW = 10;
    private static final double IMAGE_SIZE = 96;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;

    /**
     * Constructor of the AllBugemonsGridView class. It loads the FXML layout and initializes the
     * view.
     */
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

            VBox cell = createBugemonCell(bugemon);

            gridPane.add(cell, col, row);
        }
    }

    /**
     * Creates a cell for a Bugemon in the grid view, containing the image and name of the Bugemon.
     * If the Bugemon is null, it displays an unknown image and an empty name.
     * @param bugemon the BugemonDTO representing the Bugemon to be displayed in the cell
     * @return a VBox containing the image and name of the Bugemon to be displayed in the grid view
     */
    private VBox createBugemonCell(Bugemon bugemon) {
        Image image = new Image(bugemon.getSpriteURL());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        // keep image within a stackpane with fixed size so labels align perfectly
        StackPane imagePane = new StackPane(imageView);
        imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        Label nameLabel = new Label(bugemon.getName());
        nameLabel.getStyleClass().add("bugemon-cell-name");

        VBox cell = new VBox(2); // spacing exactly 2
        cell.setAlignment(Pos.CENTER);
        cell.getChildren().addAll(imagePane, nameLabel);
        cell.setUserData(bugemon);

        if (selectionChecker != null && selectionChecker.apply(bugemon)) {
            select(cell);
        } else {
            unselect(cell);
        }

        if (this.onBugemonClicked != null) {
            cell.setOnMouseClicked(e -> {
                Bugemon b = (Bugemon)cell.getUserData();
                if (b != null) {
                    this.onBugemonClicked.accept(b);
                }
            });
        }

        return cell;
    }

    /**
     * Marks the given cell as selected by changing its style to indicate selection.
     * @param cell the VBox cell to be marked as selected, containing the image and name of the
     *         Bugemon to be styled as selected
     */
    private void select(VBox cell) {
        StackPane imagePane = (StackPane)cell.getChildren().get(
                0); // TODO: Could break code with an exeption "IndexOutOfBoundsException"
        ImageView iv = (ImageView)imagePane.getChildren().get(0);
        iv.getStyleClass().add("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell");
        cell.getStyleClass().add("bugemon-cell-selected");
    }

    /**
     * Marks the given cell as unselected by changing its style to indicate it is not selected.
     * @param cell the VBox cell to be marked as unselected, containing the image and name of the
     *         Bugemon to be styled as unselected
     */
    private void unselect(VBox cell) {
        StackPane imagePane = (StackPane)cell.getChildren().get(0);
        ImageView iv = (ImageView)imagePane.getChildren().get(0);
        iv.getStyleClass().remove("bugemon-image-selected");
        cell.getStyleClass().remove("bugemon-cell-selected");
        cell.getStyleClass().add("bugemon-cell");
    }
}
